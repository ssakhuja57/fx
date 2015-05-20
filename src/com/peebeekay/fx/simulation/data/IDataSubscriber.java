package com.peebeekay.fx.simulation.data;

import com.peebeekay.fx.simulation.data.types.Tick;


public interface IDataSubscriber extends Runnable{
	
	void accept(Tick price);
	boolean isReady();

}
