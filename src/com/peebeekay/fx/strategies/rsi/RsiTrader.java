package com.peebeekay.fx.strategies.rsi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.peebeekay.fx.data.ATickDataDistributor;
import com.peebeekay.fx.data.IDataProvider;
import com.peebeekay.fx.data.OhlcDataDistributor;
import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.rates.RateStats;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.trades.ATrader;
import com.peebeekay.fx.trades.ITradeActionProvider;
import com.peebeekay.fx.trades.ITradeInfoProvider;
import com.peebeekay.fx.trades.OrderCreationException;
import com.peebeekay.fx.trades.specs.CreateTradeSpec;
import com.peebeekay.fx.trades.specs.CreateTradeSpec.CloseTradeType;
import com.peebeekay.fx.trades.specs.CreateTradeSpec.OpenTradeType;
import com.peebeekay.fx.trades.specs.TradeSpec.TradeProperty;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.RateUtils;


public class RsiTrader extends ATrader{
	
		String name;
		Pair pair;
		private RSI rsi;
		private double prevRsi;
		private int maxStopSize;
		int maxConcurrentTrades;
		Interval interval;
		
		ATickDataDistributor tDD;
		OhlcDataDistributor ohlcDD;
		
		private RateStats stats;
		
		public final int PERIOD = 14;
		public final int HIGH_MARK = 70;
		public final double LOW_MARK = 30;
		final int MIN_STOP = 3;
		
		final int LOTS = 1;
		
		private volatile Boolean isReady;
		private Boolean stillRunning;
		private Signal signal = Signal.HOLD;
		
		private enum Signal{
			HOLD,BUY,SELL
		}
		
		public RsiTrader(String name, ITradeActionProvider ap, ITradeInfoProvider ip,
				IDataProvider dp, ATickDataDistributor tDD, OhlcDataDistributor ohlcDD,
				Interval interval, Pair pair, int maxStopSize, int maxConcurrentTrades){
			super(ap, ip, name);
			
			Logger.info("initializing RSI trader " + name);
			
			this.name = name;
			this.pair = pair;
			this.tDD = tDD;
			this.ohlcDD = ohlcDD;
			this.interval = interval;
			this.maxStopSize = maxStopSize;
			this.maxConcurrentTrades = maxConcurrentTrades;
			
			stats = new RateStats(96, interval);
			
			// rsi
			Calendar nowMinusPeriod = Calendar.getInstance();
//			nowMinusPeriod.setTime(startTime.getTime());
//			nowMinusPeriod.add(Calendar.MINUTE, -period*INTERVAL.minutes);
			nowMinusPeriod.add(Calendar.HOUR, -150); // add extra in case run over weekend/holiday data
			ArrayList<OhlcPrice> historicalPrices = dp.getOhlcRows(pair, interval, nowMinusPeriod, Calendar.getInstance());
			rsi = new RSI(interval, PERIOD, true, true, historicalPrices);
			prevRsi = rsi.getValue();
			
			isReady = true;
			stillRunning = true;
			
			Logger.info("trader " + name + " initialized");
			
		}
		
		public void run(){
			// subscribe
			List<Pair> pairs = new ArrayList<Pair>();
			pairs.add(pair);
			List<Interval> intervals = new ArrayList<Interval>();
			intervals.add(interval);
			tDD.addSubscriber(this, pairs, intervals);
			ohlcDD.addSubscriber(this, pairs, intervals);
			Logger.info("trader " + name + " started");
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
			try {
//				super.tradeMgr.updateTrade(trade, new StopClose(trade, stopOffset, true)); // constant stop size
				
				// start: lines for recent extremum
				double stop = stats.getRecentExtremum(1, 3, !tradeLong, tradeLong);
				int initialOffset = (int)RateUtils.getAbsPipDistance(price.getExitPrice(tradeLong), stop);
				if(initialOffset > maxStopSize)
					initialOffset = maxStopSize;
				if(initialOffset < MIN_STOP)
					initialOffset = MIN_STOP;
				CreateTradeSpec spec = new CreateTradeSpec(pair, LOTS, tradeLong, OpenTradeType.MARKET_OPEN, CloseTradeType.STOP_CLOSE);
				spec.setTradeProperty(TradeProperty.STOP_SIZE, String.valueOf(initialOffset));
				super.createOrder(spec);
				// end: lines for recent extremum
			} catch (OrderCreationException e) {
				e.printStackTrace();
			} finally{
				signal = Signal.HOLD;
			}
		}
		
		@Override
		public void accept(OhlcPrice price) {
//			Logger.debug("received " + price.getInterval() + " at " + price.getTime());
				rsi.addDataPoint(price);
				stats.accept(price);
				
				double priorRsi = prevRsi; // just for logging
				
				signal = chooseAction();
				prevRsi = rsi.getValue();
//				Logger.debug("bid close price for " + price.getPair() + " at " + DateUtils.dateToString(price.getTime()) + ": " 
//								+ price.getBidClose() + ", and RSI: "+ prevRsi + " (previous: " + priorRsi + ")");
			}

		@Override
		public boolean okToCreateOrder() {
			if(super.getNumberOfOpenTrades(pair) >= maxConcurrentTrades)
				return false;
			return true;
		}
		

		

}
