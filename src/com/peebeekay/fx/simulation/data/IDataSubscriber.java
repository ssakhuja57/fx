package com.peebeekay.fx.simulation.data;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;


public interface IDataSubscriber{
	
	void accept(OhlcPrice price);
	void accept(Tick price);

}
