package com.peebeekay.fx.simulation.monitors.open;

import com.peebeekay.fx.simulation.data.Price;
import com.peebeekay.fx.simulation.monitors.ATradeMonitor;

public abstract class AOpenTradeMonitor extends ATradeMonitor{
	
	@Override
	public void execute(Price price){
		super.trade.open(price, this);
	}
}
