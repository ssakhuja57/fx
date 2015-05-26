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

	private int tickRow = 0;
	private Calendar tickClock;
	private Calendar OhlcClock;
	private boolean moreData = true;
	
	private Pair pair;
	private Calendar end;
	private IDataSource dataSource;
	private ArrayList<ATrader> traders = new ArrayList<ATrader>();
	
	public SimulationController(Pair pair, Calendar start, Calendar end, IDataSource dataSource){
		this.pair = pair;
		this.end = end;
		this.dataSource = dataSource;
		tickClock = Calendar.getInstance(); tickClock.setTime(start.getTime());
		OhlcClock = Calendar.getInstance(); OhlcClock.setTime(start.getTime());
	}
	
	public void addTrader(ATrader trader){
		traders.add(trader);
	}
	
	private void advanceTick(){
		Tick tick = null;
		try{
			tick = dataSource.getTickRow(tickRow);
		} catch(IndexOutOfBoundsException e){
			Logger.info("no more data in source, finished!");
			moreData = false;
			return;
		}
		tickClock.setTime(tick.getTime());
		Calendar tickTime = DateUtils.getCalendar(tick.getTime());
		Calendar tickTimeMinute = DateUtils.roundDownToMinute(tickTime); 
		//Logger.debug(OhlcClock.getTime().toString());
		if(tickTimeMinute.after(OhlcClock)){
			//Logger.debug(tickTimeMinute.getTime().toString() + " > " + OhlcClock.getTime().toString());
			OhlcClock = tickTimeMinute;
			sendOhlc();
		}
		sendTick(tick);
		tickRow++;
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
			if(interval != Interval.M30)
				continue;
			if(DateUtils.isMultipleOf(OhlcClock.getTime(), interval.minutes)){
				for(ATrader t: traders){
					ArrayList<OhlcPrice> prices = dataSource.getOhlcPrices(pair, interval, OhlcClock, OhlcClock);
					if(prices.size() > 0)
						t.accept(prices.get(0));
				}
			}
		}
	}

	@Override
	public void run() {
		while(true){
			if(tickClock.after(end) || !moreData)
				break;
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
