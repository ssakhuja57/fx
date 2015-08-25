package com.peebeekay.fx.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.utils.DateUtils;

public class OhlcDataDistributor extends ADataDistributor{

	IDataProvider dp;
	List<Pair> pairs;
	List<Interval> intervals;
	List<Timer> workers = new ArrayList<Timer>();
	
	public OhlcDataDistributor(IDataProvider dp, List<Pair> pairs, List<Interval> intervals) {
		
		this.dp = dp;
		this.pairs = pairs;
		this.intervals = intervals;
		for(Interval interval: intervals){
			if(interval == Interval.T)
				continue;
			Timer worker = new Timer();
			worker.schedule(new Distributor(interval), DateUtils.getNextIntervalTime(interval).getTime(), interval.minutes*60*1000);
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
				OhlcPrice row = dp.getOhlcRow(p, interval);
				Pair pair = row.getPair();
				for(IDataSubscriber ds: subscribers){
					if(subscriberIntervals.get(ds).contains(interval) && subscriberPairs.get(ds).contains(pair))
						ds.accept(row);
				}
			}
		}
		
	}
	
	
	

}
