package com.peebeekay.fx.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trader.ATrader;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;


public class SimulationController implements Runnable{

	private Calendar tickClock;
	private Calendar ohlcClock;
	private boolean moreData = true;
	
	private Pair pair;
	private Calendar end;
	private IDataSource dataSource;
	private ArrayList<ATrader> traders = new ArrayList<ATrader>();
	
	public SimulationController(Pair pair, Calendar start, Calendar end, IDataSource dataSource){
		Logger.debug("creating simulation controller for " + pair 
				+ " from " + DateUtils.calToString(start) + " to " + DateUtils.calToString(end));
		this.pair = pair;
		this.end = end;
		this.dataSource = dataSource;
		tickClock = Calendar.getInstance(); tickClock.setTime(start.getTime());
		ohlcClock = Calendar.getInstance(); ohlcClock.setTime(start.getTime());
	}
	
	public void addTrader(ATrader trader){
		traders.add(trader);
	}
	
	private void advanceTick(){
//		Logger.debug(tickClock.getTime().toString());
//		Logger.debug("advancing tick");
		Tick tick = null;
		try{
			tick = dataSource.getTickRow();
		} catch(Exception e){
			Logger.info("no more data in source, finished!");
			moreData = false;
			return;
		}
		tickClock.setTime(tick.getTime());
		Calendar tickTime = DateUtils.getCalendar(tick.getTime());
		Calendar tickTimeMinute = DateUtils.roundDownToMinute(tickTime); 
		//Logger.debug(OhlcClock.getTime().toString());
		if(tickTimeMinute.after(ohlcClock)){
//			Logger.debug(tickTimeMinute.getTime().toString() + " > " + ohlcClock.getTime().toString());
			ohlcClock = tickTimeMinute;
			sendOhlc();
		}
		sendTick(tick);
	}
	
	private void sendTick(Tick tick){
		for(ATrader t: traders){
			t.accept(tick);
		}
	}
	
	private void sendOhlc(){
		for(Interval interval: EnumSet.allOf(Interval.class)){
			if(interval == Interval.T)
				continue;
			if(interval != Interval.M30) //tmp
				continue;
			if(DateUtils.isMultipleOf(ohlcClock.getTime(), interval.minutes)){
				
				Calendar time = Calendar.getInstance();
				time.setTime(ohlcClock.getTime());
				time.add(Calendar.MINUTE, -interval.minutes); // to get period that just closed
				
				for(ATrader t: traders){
					OhlcPrice price = dataSource.getOhlcPrice(pair, interval, time);
					if(price != null)
						t.accept(price);
				}
			}
		}
	}

	@Override
	public void run() {
		while(true){
			if(tickClock.after(end) || !moreData){
//				Logger.debug(tickRow+"");
//				Logger.debug(tickClock.getTime().toString() + " " + moreData);
				break;
			}
			advanceTick();
		}
		for(ATrader t: traders)
			try {
				t.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
}
