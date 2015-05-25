package com.peebeekay.fx.simulation.data;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;


public interface IDataSubscriber extends Runnable{
	
	void accept(OhlcPrice price);
	void accept(Tick price);
	Boolean isReady();

}
