package com.peebeekay.fx.simulation.monitors.cancel;

import com.peebeekay.fx.simulation.data.Price;
import com.peebeekay.fx.simulation.monitors.ATradeMonitor;

public abstract class ACancelTradeMonitor extends ATradeMonitor{


	@Override
	public void execute(Price price) {
		super.trade.cancel();
	}

}
