package com.peebeekay.fx.simulation.monitors.close;

import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.monitors.ATradeMonitor;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.simulation.trades.Trade.Status;

public abstract class ACloseTradeMonitor extends ATradeMonitor{
	
	public ACloseTradeMonitor(Trade trade) {
		super(trade);
		super.validStatus = Status.OPEN;
	}

	@Override
	public void execute(Tick price){
		super.trade.close(price, this);
	}
}
