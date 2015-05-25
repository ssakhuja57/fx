package com.peebeekay.fx.simulation.monitors.cancel;

import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.monitors.ATradeMonitor;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.simulation.trades.Trade.Status;

public abstract class ACancelTradeMonitor extends ATradeMonitor{


	public ACancelTradeMonitor(Trade trade) {
		super(trade);
		super.validStatus = Status.WAITING;
	}

	@Override
	public void execute(Tick price) {
		super.trade.cancel();
	}

}
