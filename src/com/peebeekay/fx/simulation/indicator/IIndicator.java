package com.peebeekay.fx.simulation.indicator;

import com.peebeekay.fx.simulation.data.Price;

public interface IIndicator {
	
	void addDataPoint(Price p);
	double getValue();
}
