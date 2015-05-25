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
		if(!super.checkValidStatus()){
			return;
		}
		super.execute(price);
		this.cancel();
	}

	@Override
	public Boolean isReady() {
		return true;
	}

	@Override
	public void run() {
		
	}

	@Override
	public void accept(OhlcPrice price) {
		// TODO Auto-generated method stub
		
	}

	
}
