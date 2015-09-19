package com.peebeekay.fx.simulation.trader;

import java.util.ArrayList;
import java.util.Calendar;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.rates.RateStats;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.indicator.IIndicator;
import com.peebeekay.fx.simulation.monitors.close.StopClose;
import com.peebeekay.fx.simulation.monitors.open.MarketOpen;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.strategies.rsi.RSI;
import com.peebeekay.fx.utils.RateUtils;

public class SimpleRSITrader extends ATrader implements Runnable{
	
	private IIndicator rsi;
	private Calendar startTime;
	private Pair pair;
	private int stopOffset;
	private Interval interval;
	public final int period = 14;
	public final int HIGH_MARK = 70;
	public final double LOW_MARK = 30;
	private double prevRsi;
	private volatile Boolean isReady;
	private Boolean stillRunning;
	private Signal signal = Signal.HOLD;
	private RateStats stats = new RateStats(96, interval);;
	
	private enum Signal{
		HOLD,BUY,SELL
	}
	
	public SimpleRSITrader(String name, Interval interval, String outputFolder, Pair pair, IDataSource ds, 
			Calendar startTime, int stopOffset, int maxConcurrentTrades){
		super(ds, name, outputFolder, maxConcurrentTrades);
		this.interval = interval;
		this.startTime = startTime;
		this.pair = pair;
		this.stopOffset = stopOffset;
		
		Calendar nowMinusPeriod = Calendar.getInstance();
		nowMinusPeriod.setTime(startTime.getTime());
		//nowMinusPeriod.add(Calendar.MINUTE, -period*INTERVAL.minutes);
		nowMinusPeriod.add(Calendar.HOUR, -120); // add extra in case run over weekend data
		
		ArrayList<OhlcPrice> historicalPrices = ds.getOhlcPrices(pair, interval, nowMinusPeriod, startTime);
		rsi = new RSI(interval, period, true, true, historicalPrices);
		prevRsi = rsi.getValue();
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
//		Logger.debug("received tick " + price.getTime());
		super.tradeMgr.accept(price);
		if(signal == Signal.HOLD)
			return;
		
		boolean tradeLong = (signal == Signal.BUY) ? true : false;
		Trade trade = null;
		try {
			trade = super.tradeMgr.createTrade(pair, tradeLong, 1000);
			super.tradeMgr.updateTrade(trade, new MarketOpen(trade));
//			super.tradeMgr.updateTrade(trade, new StopClose(trade, stopOffset, true)); // constant stop size
			super.tradeMgr.addTradeNotes(trade, "open_rsi: " + prevRsi);
			// start: lines for recent extremum
			double stop = stats.getRecentExtremum(1, 3, !tradeLong, tradeLong);
			int initialOffset = (int)RateUtils.getAbsPipDistance(price.getExitPrice(tradeLong), stop);
			if(initialOffset > stopOffset)
				initialOffset = stopOffset;
			//super.tradeMgr.updateTrade(trade, new StopClose(trade, stopOffset, initialOffset)); // for adjusting stop
			super.tradeMgr.updateTrade(trade, new StopClose(trade, stopOffset, true)); // for not adjusting stop
			// end: lines for recent extremum
			
		} catch (TradeCreationException e) {
		} finally{
			signal = Signal.HOLD;
		}
	}
	
	@Override
	public void accept(OhlcPrice price) {
//		Logger.debug("received " + price.getInterval() + " at " + price.getTime());
		if(price.getInterval() == interval){
			rsi.addDataPoint(price);			
			signal = chooseAction();
			prevRsi = rsi.getValue();
			stats.accept(price); // temp
//			Logger.debug(price.getTime().toString() + ":" + prevRsi);
		}
	}
	


	@Override
	public void run() {

	}

	
}
