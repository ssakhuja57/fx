package com.peebeekay.fx.simulation.indicator;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;


public interface IIndicator {
	
	public enum Signal{
		BUY, SELL;
	}
	
	public double getValue();
	public void addDataPoint(Tick price);
	public void addDataPoint(OhlcPrice price);
	

	
}
