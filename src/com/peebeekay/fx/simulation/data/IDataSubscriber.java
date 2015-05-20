package com.peebeekay.fx.simulation.data;

import java.util.Calendar;
import java.util.HashMap;

public interface IDataSubscriber extends Runnable{
	
	void accept(Price prices);
	Boolean isReady();

}
