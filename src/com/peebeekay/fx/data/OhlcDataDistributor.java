package com.peebeekay.fx.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;

public class OhlcDataDistributor extends ADataDistributor{

	IDataProvider dp;
	List<Pair> pairs;
	List<Interval> intervals;
	List<Timer> workers = new ArrayList<Timer>();
	
	final int DELAY = 5;
	final int MAX_ATTEMPTS = 3;
	
	public OhlcDataDistributor(IDataProvider dp, List<Pair> pairs, List<Interval> intervals) {
		
		this.dp = dp;
		this.pairs = pairs;
		this.intervals = intervals;
		for(Interval interval: intervals){
			if(interval == Interval.T)
				continue;
			Timer worker = new Timer();
			Calendar initial = DateUtils.getNextIntervalTime(interval);
			initial.add(Calendar.SECOND, DELAY); // give broker a few extra seconds to publish ohlc data
			worker.schedule(new Distributor(interval), initial.getTime(), interval.minutes*60*1000);
			workers.add(worker);
		}
	}
	
	@Override
	public void close(){
		super.unsubscribeAll();
		for(Timer worker: workers)
			worker.cancel();
	}
	
	class Distributor extends TimerTask{
		
		Interval interval;
		
		Distributor(Interval interval){
			this.interval = interval;
		}
		
		@Override
		public void run() {
			for(Pair p: pairs){
				Calendar time = DateUtils.getLastIntervalTime(interval);
				time.add(Calendar.MINUTE, -interval.minutes); // get period that just closed
				OhlcPrice row = null;
				int attempts = 0;
				while(attempts < MAX_ATTEMPTS){
					try {
						row = dp.getOhlcRow(p, interval, time);
						break;
					} catch (DataNotFoundException e) {
						Logger.error(p + " " + interval + " data not found for " + DateUtils.calToString(time) + ", retrying...");
						attempts++;
					}
				}
				
				if(attempts == MAX_ATTEMPTS){
					Logger.error("could not find data, skipping...");
					continue;
				}
				
				Logger.debug("sending " + interval + " ohlc data for " + DateUtils.calToString(time));
				
				for(IDataSubscriber ds: subscribers){
					if(subscriberIntervals.get(ds).contains(interval) && subscriberPairs.get(ds).contains(p))
						ds.accept(row);
				}
			}
		}
		
	}
	
	
	

}
