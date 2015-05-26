package com.peebeekay.fx.simulation.monitors.open;

import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.monitors.ATradeMonitor;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.simulation.trades.Trade.Status;

public abstract class AOpenTradeMonitor extends ATradeMonitor{
	
	public AOpenTradeMonitor(Trade trade) {
		super(trade);
		super.validStatus = Status.WAITING;
	}

	@Override
	public void execute(Tick price){
		super.trade.open(price, this);
		this.cancel();
	}
}
