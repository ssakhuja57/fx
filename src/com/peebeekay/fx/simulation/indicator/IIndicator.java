package com.peebeekay.fx.simulation.indicator;


public interface IIndicator {
	
	public enum Signal{
		BUY, SELL;
	}
	
	public double getValue();
	

	
}
