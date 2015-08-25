package com.peebeekay.fx.strategies.rsi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.peebeekay.fx.data.IDataProvider;
import com.peebeekay.fx.data.OhlcDataDistributor;
import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.rates.RateStats;
import com.peebeekay.fx.simulation.data.distributors.TickDataDistributor;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.indicator.RSI;
import com.peebeekay.fx.simulation.monitors.close.StopClose;
import com.peebeekay.fx.simulation.monitors.open.MarketOpen;
import com.peebeekay.fx.simulation.trader.TradeCreationException;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.trades.ATrader;
import com.peebeekay.fx.trades.ITradeActionProvider;
import com.peebeekay.fx.trades.ITradeInfoProvider;
import com.peebeekay.fx.trades.specs.CreateTradeSpec;
import com.peebeekay.fx.trades.specs.CreateTradeSpec.CloseTradeType;
import com.peebeekay.fx.trades.specs.CreateTradeSpec.OpenTradeType;
import com.peebeekay.fx.utils.RateUtils;


public class RsiTrader extends ATrader{
	
		Pair pair;
		private RSI rsi;
		private double prevRsi;
		private int maxStopSize;
		int maxConcurrentTrades;
		Interval interval;
		
		TickDataDistributor tDD;
		OhlcDataDistributor ohlcDD;
		
		private RateStats stats;
		
		public final int PERIOD = 14;
		public final int HIGH_MARK = 70;
		public final double LOW_MARK = 30;
		
		private volatile Boolean isReady;
		private Boolean stillRunning;
		private Signal signal = Signal.HOLD;
		
		private enum Signal{
			HOLD,BUY,SELL
		}
		
		public RsiTrader(String name, ITradeActionProvider ap, ITradeInfoProvider ip, 
				TickDataDistributor tDD, OhlcDataDistributor ohlcDD, IDataProvider dataProvider,
				Interval interval, Pair pair, int maxStopSize, int maxConcurrentTrades){
			super(ap, ip, name);
			this.pair = pair;
			this.tDD = tDD;
			this.ohlcDD = ohlcDD;
			this.interval = interval;
			this.maxStopSize = maxStopSize;
			this.maxConcurrentTrades = maxConcurrentTrades;
			
			stats = new RateStats(96, interval);
			
			
			Calendar nowMinusPeriod = Calendar.getInstance();
//			nowMinusPeriod.setTime(startTime.getTime());
//			nowMinusPeriod.add(Calendar.MINUTE, -period*INTERVAL.minutes);
			nowMinusPeriod.add(Calendar.HOUR, -120); // add extra in case run over weekend/holiday data
			
			ArrayList<OhlcPrice> historicalPrices = dataProvider.getOhlcRows(pair, interval, nowMinusPeriod, Calendar.getInstance());
			rsi = new RSI(interval, PERIOD, true, true, historicalPrices);
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
//			Logger.debug("received tick " + price.getTime());
			if(signal == Signal.HOLD)
				return;
			
			boolean tradeLong = (signal == Signal.BUY) ? true : false;
			Trade trade = null;
			try {
//				super.tradeMgr.updateTrade(trade, new StopClose(trade, stopOffset, true)); // constant stop size
				
				// start: lines for recent extremum
				double stop = stats.getRecentExtremum(1, 3, !tradeLong, tradeLong);
				int initialOffset = (int)RateUtils.getAbsPipDistance(price.getExitPrice(tradeLong), stop);
				if(initialOffset > maxStopSize)
					initialOffset = maxStopSize;
				CreateTradeSpec spec = new CreateTradeSpec(pair, lots, tradeLong, OpenTradeType.MARKET_OPEN, CloseTradeType.STOP_CLOSE, tradeProperties)
				super.tradeMgr.updateTrade(trade, new StopClose(trade, stopOffset, initialOffset));
				// end: lines for recent extremum
				
			} catch (TradeCreationException e) {
			} finally{
				signal = Signal.HOLD;
			}
		}
		
		@Override
		public void accept(OhlcPrice price) {
//			Logger.debug("received " + price.getInterval() + " at " + price.getTime());
				rsi.addDataPoint(price);			
				signal = chooseAction();
				prevRsi = rsi.getValue();
				stats.accept(price); // temp
//				Logger.debug(price.getTime().toString() + ":" + prevRsi);
			}

		@Override
		public boolean okToCreateOrder() {
			if(super.getNumberOfOpenTrades() >= maxConcurrentTrades)
				return false;
			return true;
		}
		

		

}
