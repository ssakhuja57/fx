package com.peebeekay.fx.data;

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
	List<Pair> validPairs;
	Interval interval;
	private Timer worker;
	
	public OhlcDataDistributor(IDataProvider dp, List<Pair> pairs, Interval interval) {
		if(interval == Interval.T)
			throw new RuntimeException();
		this.dp = dp;
		this.validPairs = pairs;
		this.interval = interval;
		worker.schedule(new Distributor(), DateUtils.getNextIntervalTime(interval).getTime(), interval.minutes*60*1000);
	}
	
	@Override
	public void close(){
		super.unsubscribeAll();
		worker.cancel();
	}
	
	class Distributor extends TimerTask{
		@Override
		public void run() {
			for(Pair p: validPairs){
				OhlcPrice row = dp.getOhlcRow(p, interval);
				for(IDataSubscriber ds: subscribers)
					ds.accept(row);
			}
		}
		
	}
	
	
	

}
