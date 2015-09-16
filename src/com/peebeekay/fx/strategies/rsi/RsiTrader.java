package com.peebeekay.fx.strategies.rsi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.peebeekay.fx.brokers.fxcm.FxcmSessionManager;
import com.peebeekay.fx.data.ATickDataDistributor;
import com.peebeekay.fx.data.IDataProvider;
import com.peebeekay.fx.data.OhlcDataDistributor;
import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.rates.RateStats;
import com.peebeekay.fx.session.SessionDependent;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.trades.ATrader;
import com.peebeekay.fx.trades.IAccountInfoProvider;
import com.peebeekay.fx.trades.ITradeActionProvider;
import com.peebeekay.fx.trades.ITradeInfoProvider;
import com.peebeekay.fx.trades.OrderCreationException;
import com.peebeekay.fx.trades.StopAdjuster;
import com.peebeekay.fx.trades.Trade;
import com.peebeekay.fx.trades.TradeNotFoundException;
import com.peebeekay.fx.trades.specs.CreateTradeSpec;
import com.peebeekay.fx.trades.specs.CreateTradeSpec.CloseTradeType;
import com.peebeekay.fx.trades.specs.CreateTradeSpec.OpenTradeType;
import com.peebeekay.fx.trades.specs.TradeSpec.TradeProperty;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.RateUtils;


public class RsiTrader extends ATrader implements SessionDependent{
	
		FxcmSessionManager fx;
	
		String name;
		Pair pair;
		private RSI rsi;
		private double prevRsi;
		private int maxStopSize;
		int maxConcurrentTrades;
		Interval interval;
		
		ATickDataDistributor tDD;
		OhlcDataDistributor ohlcDD;
		IDataProvider dp;
		IAccountInfoProvider aip;
		
		ITradeActionProvider ap;
		ITradeInfoProvider info;
		
		StopAdjuster adjuster;
		
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
		
		public RsiTrader(String name, FxcmSessionManager fx, IAccountInfoProvider aip, ITradeActionProvider ap, 
				ITradeInfoProvider ip,
				IDataProvider dp, ATickDataDistributor tDD, OhlcDataDistributor ohlcDD,
				Interval interval, Pair pair, int maxStopSize, int maxConcurrentTrades){
			super(ap, ip, name);
			
			Logger.info("initializing RSI trader " + name);
			
			this.name = name;
			this.fx = fx;
			this.pair = pair;
			this.tDD = tDD;
			this.ohlcDD = ohlcDD;
			this.aip = aip;
			this.dp = dp;
			this.ap = ap;
			this.info = ip;
			this.interval = interval;
			this.maxStopSize = maxStopSize;
			this.maxConcurrentTrades = maxConcurrentTrades;
			
			stats = new RateStats(96, interval);
			
		}
		
		public void run(){
			
			fx.registerDependent(this);
			
			// rsi
			Calendar nowMinusPeriod = Calendar.getInstance();
//			nowMinusPeriod.setTime(startTime.getTime());
//			nowMinusPeriod.add(Calendar.MINUTE, -period*INTERVAL.minutes);
			nowMinusPeriod.add(Calendar.HOUR, -150); // add extra in case run over weekend/holiday data
			ArrayList<OhlcPrice> historicalPrices = dp.getOhlcRows(pair, interval, nowMinusPeriod, Calendar.getInstance());
			rsi = new RSI(interval, PERIOD, true, true, historicalPrices);
			prevRsi = rsi.getValue();
			
			Logger.info("trader " + name + " initialized");
			
			// subscribe
			List<Pair> pairs = new ArrayList<Pair>();
			pairs.add(pair);
			List<Interval> intervals = new ArrayList<Interval>();
			intervals.add(interval);
			tDD.addSubscriber(this, pairs, intervals);
			ohlcDD.addSubscriber(this, pairs, intervals);
			Logger.info("trader " + name + " started");
			
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
			if(adjuster != null)
				adjuster.accept(price);
		}
		
		void execute(){
//			signal = Signal.SELL; // temp
			if(signal == Signal.HOLD)
				return;
			
			boolean tradeLong = (signal == Signal.BUY) ? true : false;
			try {
//				super.tradeMgr.updateTrade(trade, new StopClose(trade, stopOffset, true)); // constant stop size
				
				// start: lines for recent extremum
				double stop = stats.getRecentExtremum(1, 3, !tradeLong, tradeLong);
				Tick t = dp.getTick(pair);
//				int initialOffset = (int)RateUtils.getAbsPipDistance(price.getExitPrice(tradeLong), stop);
				int stopSize = (int)RateUtils.getAbsPipDistance(t.getExitPrice(tradeLong), stop);
				
				if(RateUtils.isEqualOrBetter(t, stop, tradeLong, true))
					stopSize = MIN_STOP; // if tick is better price than recent extremum, then use min stop size
				else if(stopSize > maxStopSize)
					stopSize = maxStopSize;
				else if(stopSize < MIN_STOP)
					stopSize = MIN_STOP;
				CreateTradeSpec spec = new CreateTradeSpec(pair, getLots(), tradeLong, OpenTradeType.MARKET_OPEN, CloseTradeType.STOP_CLOSE);
				spec.setTradeProperty(TradeProperty.STOP_SIZE, String.valueOf(stopSize));
				if(super.createOrder(spec)){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					adjuster = new StopAdjuster(ap, info, super.getOpenTrades(pair).get(0), stopSize, maxStopSize);
				}
				// end: lines for recent extremum
			} catch (OrderCreationException e) {
				e.printStackTrace();
			} catch (TradeNotFoundException e) {
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
				
//				double priorRsi = prevRsi; // just for logging
				
				signal = chooseAction();
				prevRsi = rsi.getValue();
//				Logger.debug("bid close price for " + price.getPair() + " at " + DateUtils.dateToString(price.getTime()) + ": " 
//								+ price.getBidClose() + ", and RSI: "+ prevRsi + " (previous: " + priorRsi + ")");
				execute();
			}

		@Override
		public boolean okToCreateOrder() {
			if(super.getNumberOfOpenTrades(pair) >= maxConcurrentTrades){
				Logger.info("rejecting attempt to create trade for " + pair + " since at max concurrent trades of " + maxConcurrentTrades);
				return false;
			}
			return true;
		}

		@Override
		public void reconnect() {
			run();
		}
		
		private int getLots(){
			return LOTS;
//			int openTrades = super.getNumberOfOpenTrades();
//			double neededMargin = aip.getPercentMaxAccountUse()/(openTrades+1);
//			double availableMargin = aip.getAvailableUsablePercentAccountBalance();
//			if (availableMargin >= neededMargin)
//				return aip.getLots(pair, availableMargin*aip.getTotalUsableAccountBalance());
//			
//			double marginToOpen = neededMargin - availableMargin;
//			List<Trade> trades = super.getOpenTrades();
//			List<Trade> tradesToAdjust = new ArrayList<Trade>();
//			for(Trade trade: trades){
//				double percentMargin = trade.getLots()/aip.getTotalUsableAccountBalance();
//				if(percentMargin > neededMargin)
//					tradesToAdjust.add(trade);
//			}
//			
//			double tradesToAdjustTargetMargin = marginToOpen/tradesToAdjust.size();
//			
//			for(Trade trade: tradesToAdjust){
//				double percentMargin = trade.getLots()/aip.getTotalUsableAccountBalance();
//				double toClose = percentMargin - tradesToAdjustTargetMargin;
//				int lots = aip.getLots(pair, aip.getTotalUsableAccountBalance()*toClose);
//				fx.partialClose(trade, lots);
//			}
//			
//			return aip.getLots(pair, aip.getTotalUsableAccountBalance()*neededMargin);
		}
		

}
