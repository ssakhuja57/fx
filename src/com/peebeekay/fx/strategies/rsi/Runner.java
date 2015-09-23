package com.peebeekay.fx.strategies.rsi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.peebeekay.fx.brokers.fxcm.FxcmSessionManager;
import com.peebeekay.fx.brokers.fxcm.FxcmTickDataDistributor;
import com.peebeekay.fx.brokers.fxcm.FxcmTradeInfoProvider;
import com.peebeekay.fx.data.ATickDataDistributor;
import com.peebeekay.fx.data.OhlcDataDistributor;
import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.session.Credentials;
import com.peebeekay.fx.session.Credentials.LoginProperties;
import com.peebeekay.fx.trades.ITradeInfoProvider;
import com.peebeekay.fx.trades.TradeNotFoundException;

public class Runner {

	static List<Credentials> creds = new ArrayList<Credentials>();
	static Map<Interval,int[]> maxStops = new HashMap<Interval,int[]>();
	static Map<Interval,int[]> minStops = new HashMap<Interval,int[]>();
	static{
		creds.add(new Credentials("D172901772001", "600", "Demo", new String[]{"2904130"}));
		creds.add(new Credentials("D172929194001", "9819", "Demo", new String[]{"2931551"}));
		
		maxStops.put(Interval.M1, new int[]{7,7,7,7,7,7});
		maxStops.put(Interval.M15, new int[]{8,8,8,8,8,8});
		maxStops.put(Interval.M30, new int[]{15,20,20,20,20,20});
		
		minStops.put(Interval.M1, new int[]{3,3,3,3,3,3});
		minStops.put(Interval.M15, new int[]{2,2,2,2,2,2});
		minStops.put(Interval.M30, new int[]{3,3,3,3,3,3});
	}
	
	static FxcmSessionManager getSession(Credentials creds){
		creds.setProperty(LoginProperties.AUTO_RECONNECT_ATTEMPTS, "10");
		return new FxcmSessionManager(creds, .9);
	}
	
	
	static void standard(Interval interval, int credsNum){
		
		List<Pair> pairs = new ArrayList<Pair>();
		pairs.add(Pair.EURUSD);
		pairs.add(Pair.USDJPY);
		pairs.add(Pair.GBPUSD);
		pairs.add(Pair.AUDUSD);
		pairs.add(Pair.USDCAD);
		pairs.add(Pair.NZDUSD);
		
		List<Interval> intervals = new ArrayList<Interval>();
		intervals.add(interval);
//		intervals.add(Interval.M1); // temporary to test if ohlc data is being retrieved without error
		
		
		FxcmSessionManager fx = getSession(creds.get(credsNum));
		ATickDataDistributor tDD = new FxcmTickDataDistributor(fx);
		OhlcDataDistributor ohlcDD = new OhlcDataDistributor(fx, pairs, intervals);
		ITradeInfoProvider ip = new FxcmTradeInfoProvider(fx);
		
		int[] maxStopSizes = maxStops.get(interval);
		int[] minStopSizes = minStops.get(interval);
		
		new RsiTrader("test1", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(0), minStopSizes[0], maxStopSizes[0], 1).run();
		new RsiTrader("test2", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(1), minStopSizes[1], maxStopSizes[1], 1).run();
		new RsiTrader("test3", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(2), minStopSizes[2], maxStopSizes[2], 1).run();
		new RsiTrader("test4", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(3), minStopSizes[3], maxStopSizes[3], 1).run();
		new RsiTrader("test5", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(4), minStopSizes[4], maxStopSizes[4], 1).run();
		new RsiTrader("test6", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(5), minStopSizes[5], maxStopSizes[5], 1).run();
		
//		try {
//			CreateTradeSpec spec = new CreateTradeSpec(Pair.EURUSD, 1, true, OpenTradeType.MARKET_OPEN, CloseTradeType.STOP_CLOSE);
//			spec.setTradeProperty(TradeProperty.STOP_SIZE, "5");
//			fx.createOrder(spec);
//			Thread.sleep(3000);
//			Order stop = ip.getOrder(Pair.EURUSD);
//			fx.adjustStop(stop, 1.12000);
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TradeNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OrderCreationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		tDD.start();
		ohlcDD.start();

	}
	
	public static void main(String[] args) throws TradeNotFoundException, InterruptedException{
		Interval interval = Interval.valueOf(args[0]);
		int credsNum = Integer.parseInt(args[1]);
//		Interval interval = Interval.M1;
//		int credsNum = 0;
		standard(interval, credsNum);
	}
	
}
