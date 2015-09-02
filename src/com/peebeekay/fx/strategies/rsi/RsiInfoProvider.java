package com.peebeekay.fx.strategies.rsi;

import java.util.ArrayList;
import java.util.List;

import com.peebeekay.fx.brokers.fxcm.FxcmSessionManager;

public class RsiInfoProvider {

	FxcmSessionManager fx;
	int maxAccountUtilPercent;
	
	List<RsiTrader> traders = new ArrayList<RsiTrader>();
	
	public RsiInfoProvider(FxcmSessionManager fx, int maxAccountUtilPercent){
		this.fx = fx;
		this.maxAccountUtilPercent = maxAccountUtilPercent;
	}
	
	public void addTrader(RsiTrader trader){
		traders.add(trader);
	}
}
