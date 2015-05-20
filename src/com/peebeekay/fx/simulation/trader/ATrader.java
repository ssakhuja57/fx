package com.peebeekay.fx.simulation.trader;

import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.distributors.ADataDistributor;
import com.peebeekay.fx.simulation.data.types.Tick;

public abstract class ATrader implements IDataSubscriber {

	ADataDistributor ds;
	Boolean isReady;
	
	public ATrader(ADataDistributor ds) {
		this.ds = ds;
		this.isReady = true;
	}
	@Override
	public abstract void accept(Tick prices);
	@Override
	public abstract Boolean isReady();
}
