package com.peebeekay.fx.simulation.monitors.open;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trades.Trade;

public class MarketOpen extends AOpenTradeMonitor{

	public MarketOpen(Trade trade) {
		super(trade);
	}

	@Override
	public void accept(Tick price) {
		if(!super.checkValid())
			return;
		super.execute(price);
	}

	@Override
	public void run() {
		
	}

	@Override
	public void accept(OhlcPrice price) {
		// TODO Auto-generated method stub
		
	}

	
}
