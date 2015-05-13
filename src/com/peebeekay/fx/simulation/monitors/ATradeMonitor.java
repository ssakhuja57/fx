package com.peebeekay.fx.simulation.monitors;

import com.peebeekay.fx.simulation.data.Price;
import com.peebeekay.fx.simulation.trades.Trade;

public abstract class ATradeMonitor {
	
	public Trade trade;
	private boolean isLong;
	protected boolean isActive;
	
	public abstract void acceptData(Price price);
	
	public abstract void execute(Price price);
	
	public void start(){
		isActive = true;
	}
	
	public void cancel(){
		isActive = false;
	}
	
	public boolean getIsLong(){
		return isLong;
	}

}
