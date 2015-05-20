package com.peebeekay.fx.simulation.indicator;

import com.peebeekay.fx.simulation.data.types.Tick;

public interface IIndicator {
	
	void addDataPoint(Tick p);
	double getValue();
}
