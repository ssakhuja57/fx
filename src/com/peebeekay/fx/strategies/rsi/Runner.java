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
	static Map<Interval,int[]> stops = new HashMap<Interval,int[]>();
	static{
		creds.add(new Credentials("D172901772001", "600", "Demo", new String[]{"2904130"}));
		creds.add(new Credentials("D172929194001", "9819", "Demo", new String[]{"2931551"}));
		
		stops.put(Interval.M1, new int[]{5,5,5,5,5,5});
		stops.put(Interval.M15, new int[]{15,20,20,20,20,20});
		stops.put(Interval.M30, new int[]{15,20,20,20,20,20});
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
		
		int[] maxStops = stops.get(interval);
		
		new RsiTrader("test1", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(0), maxStops[0], 1).run();
		new RsiTrader("test2", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(1), maxStops[1], 1).run();
		new RsiTrader("test3", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(2), maxStops[2], 1).run();
		new RsiTrader("test4", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(3), maxStops[3], 1).run();
		new RsiTrader("test5", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(4), maxStops[4], 1).run();
		new RsiTrader("test6", fx, fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(5), maxStops[5], 1).run();
		
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
