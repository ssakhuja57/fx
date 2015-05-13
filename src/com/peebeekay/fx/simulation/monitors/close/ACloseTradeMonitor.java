package com.peebeekay.fx.simulation.monitors.close;

import com.peebeekay.fx.simulation.data.Price;
import com.peebeekay.fx.simulation.monitors.ATradeMonitor;

public abstract class ACloseTradeMonitor extends ATradeMonitor{
	
	@Override
	public void execute(Price price){
		super.trade.close(price, this);
	}
}
