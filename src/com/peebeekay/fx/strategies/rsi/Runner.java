package com.peebeekay.fx.strategies.rsi;

import java.util.ArrayList;
import java.util.List;

import com.peebeekay.fx.brokers.fxcm.FxcmSessionManager;
import com.peebeekay.fx.brokers.fxcm.FxcmTickDataDistributor;
import com.peebeekay.fx.data.ATickDataDistributor;
import com.peebeekay.fx.data.OhlcDataDistributor;
import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.session.Credentials;

public class Runner {

	static FxcmSessionManager getSession(){
		Credentials creds = new Credentials("D172901772001", "600", "Demo", new String[]{"2904130", ""});
		return new FxcmSessionManager(creds, null);
	}
	
	static void standard(){
		
		Interval interval = Interval.M30;
		
		List<Pair> pairs = new ArrayList<Pair>();
		pairs.add(Pair.EURUSD);
		pairs.add(Pair.USDJPY);
		pairs.add(Pair.GBPUSD);
		pairs.add(Pair.AUDUSD);
		pairs.add(Pair.USDCAD);
		pairs.add(Pair.NZDUSD);
		
		List<Interval> intervals = new ArrayList<Interval>();
		intervals.add(interval);
		
		
		FxcmSessionManager fx = getSession();
		ATickDataDistributor tDD = new FxcmTickDataDistributor(fx);
		OhlcDataDistributor ohlcDD = new OhlcDataDistributor(fx, pairs, intervals);
		
		RsiTrader t1 = new RsiTrader("test1", fx, fx, fx, tDD, ohlcDD, interval, pairs.get(0), 9999, 1);
		RsiTrader t2 = new RsiTrader("test2", fx, fx, fx, tDD, ohlcDD, interval, pairs.get(1), 9999, 1);
		RsiTrader t3 = new RsiTrader("test3", fx, fx, fx, tDD, ohlcDD, interval, pairs.get(2), 9999, 1);
		RsiTrader t4 = new RsiTrader("test4", fx, fx, fx, tDD, ohlcDD, interval, pairs.get(3), 9999, 1);
		RsiTrader t5 = new RsiTrader("test5", fx, fx, fx, tDD, ohlcDD, interval, pairs.get(4), 9999, 1);
		RsiTrader t6 = new RsiTrader("test6", fx, fx, fx, tDD, ohlcDD, interval, pairs.get(5), 9999, 1);
		

	}
	
	public static void main(String[] args){
		standard();
	}
	
}