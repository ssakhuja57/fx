package com.peebeekay.fx.strategies.rsi;

import java.util.ArrayList;
import java.util.List;

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

public class Runner {

	static FxcmSessionManager getSession(){
		Credentials creds = new Credentials("D172901772001", "600", "Demo", new String[]{"2904130", ""});
		creds.setProperty(LoginProperties.AUTO_RECONNECT_ATTEMPTS, "10");
		return new FxcmSessionManager(creds);
	}
	
	static void standard(){
		
		int defStop = 15;
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
//		intervals.add(Interval.M1); // temporary to test if ohlc data is being retrieved without error
		
		
		FxcmSessionManager fx = getSession();
		ATickDataDistributor tDD = new FxcmTickDataDistributor(fx);
		OhlcDataDistributor ohlcDD = new OhlcDataDistributor(fx, pairs, intervals);
		ITradeInfoProvider ip = new FxcmTradeInfoProvider(fx);
		
		new RsiTrader("test1", fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(0), defStop, 1).run();
		new RsiTrader("test2", fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(1), defStop, 1).run();
		new RsiTrader("test3", fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(2), defStop, 1).run();
		new RsiTrader("test4", fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(3), defStop, 1).run();
		new RsiTrader("test5", fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(4), defStop, 1).run();
		new RsiTrader("test6", fx, fx, ip, fx, tDD, ohlcDD, interval, pairs.get(5), defStop, 1).run();
		

	}
	
	public static void main(String[] args){
		standard();
	}
	
}
