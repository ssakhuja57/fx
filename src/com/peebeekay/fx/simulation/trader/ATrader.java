package com.peebeekay.fx.simulation.trader;

import java.io.IOException;

import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;

public abstract class ATrader implements IDataSubscriber {

	IDataSource ds;
	Boolean isReady;
	public TradeManager tradeMgr;
	
	String name;
	
	
	public ATrader(IDataSource ds, String name, String outputFolder, int maxConcurrentTrades) {
		this.ds = ds;
		this.name = name;
		tradeMgr = new TradeManager(outputFolder, name, maxConcurrentTrades);
		this.isReady = true;
	}
	
	public void close() throws IOException{
		tradeMgr.logResults();
	}
	
	@Override
	public abstract void accept(Tick price);
	@Override
	public abstract void accept(OhlcPrice price);
	
	
}
