package com.peebeekay.fx.simulation.trader;

import com.peebeekay.fx.simulation.data.ADataSource;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.Price;

public abstract class ATrader implements IDataSubscriber {

	ADataSource ds;
	Boolean isReady;
	
	public ATrader(ADataSource ds) {
		this.ds = ds;
		this.isReady = true;
	}
	@Override
	public abstract void accept(Price prices);
	@Override
	public abstract Boolean isReady();
}
