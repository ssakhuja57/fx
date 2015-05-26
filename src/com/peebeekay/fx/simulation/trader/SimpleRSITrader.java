package com.peebeekay.fx.simulation.trader;

import java.util.ArrayList;
import java.util.Calendar;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.indicator.RSI;
import com.peebeekay.fx.simulation.monitors.close.StopMonitor;
import com.peebeekay.fx.simulation.monitors.open.MarketOpen;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.utils.Logger;

public class SimpleRSITrader extends ATrader implements Runnable{
	
	private RSI rsi;
	private Calendar startTime;
	private Pair pair;
	private int stopOffset;
	public final Interval INTERVAL = Interval.M30;
	public final int period = 14;
	public final int HIGH_MARK = 70;
	public final double LOW_MARK = 30;
	private double prevRsi;
	private double pointsReceived;
	private volatile Boolean isReady;
	private Boolean stillRunning;
	private Signal signal = Signal.HOLD;
	
	private enum Signal{
		HOLD,BUY,SELL
	}
	
	public SimpleRSITrader(String name, String outputFolder, Pair pair, IDataSource ds, Calendar startTime, int stopOffset){
		super(ds, name, outputFolder);
		this.startTime = startTime;
		this.pair = pair;
		this.stopOffset = stopOffset;
		
		Calendar nowMinusPeriod = Calendar.getInstance();
		nowMinusPeriod.setTime(startTime.getTime());
		//nowMinusPeriod.add(Calendar.MINUTE, -period*INTERVAL.minutes);
		nowMinusPeriod.add(Calendar.HOUR, -120); // add extra in case run over weekend data
		
		ArrayList<OhlcPrice> historicalPrices = ds.getOhlcPrices(pair, INTERVAL, nowMinusPeriod, startTime);
		rsi = new RSI(INTERVAL, period, true, true, historicalPrices);
		prevRsi = rsi.getValue();
		pointsReceived = 0;
		isReady = true;
		stillRunning = true;
	}

	public Signal chooseAction() {
		if(prevRsi <= LOW_MARK && rsi.getValue() > LOW_MARK )
			return Signal.BUY;
		if(prevRsi >= HIGH_MARK && rsi.getValue() < HIGH_MARK)
			return Signal.SELL;
		return Signal.HOLD;
	}
	
	@Override
	public void accept(Tick price) {
		//Logger.debug("received tick " + price.getTime());
		super.tradeMgr.accept(price);
		if(signal == Signal.HOLD)
			return;
		
		boolean tradeLong = true;
		if(signal == Signal.SELL)
			tradeLong = false;
		Trade trade = super.tradeMgr.createTrade(pair, tradeLong, 1000);
		super.tradeMgr.updateTrade(trade, new MarketOpen(trade));
		super.tradeMgr.updateTrade(trade, new StopMonitor(trade, stopOffset, true));
		signal = Signal.HOLD;
	}
	
	@Override
	public void accept(OhlcPrice price) {
		//Logger.debug("received " + price.getInterval() + " at " + price.getTime());
		if(price.getInterval() == INTERVAL){
			rsi.addDataPoint(price);
			pointsReceived++;			
			signal = chooseAction();
			prevRsi = rsi.getValue();
		}
//		Logger.debug(prevRsi+"");
	}
	

	@Override
	public Boolean isReady() {
		return isReady;
	}
	

	@Override
	public void run() {

	}

	
}
