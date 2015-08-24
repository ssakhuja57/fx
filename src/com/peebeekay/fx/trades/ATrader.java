package com.peebeekay.fx.trades;

import java.io.IOException;

import com.peebeekay.fx.data.ADataDistributor;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trader.TradeManager;

public abstract class ATrader implements IDataSubscriber {

	public TradeManager tradeMgr;
	ITradeActionProvider tradeProvider;
	String name;
	
	
	public ATrader(ITradeActionProvider tradeProvider, ADataDistributor tickDataProvider, String name, String outputFolder, int maxConcurrentTrades) {
		tickDataProvider.addSubscriber(this);
		this.tradeProvider = tradeProvider;
		this.name = name;
		tradeMgr = new TradeManager(outputFolder, name, maxConcurrentTrades);
	}
	
	public void close() throws IOException{
		tradeMgr.logResults();
	}
	
	@Override
	public abstract void accept(Tick price);
	@Override
	public abstract void accept(OhlcPrice price);
	
	
}