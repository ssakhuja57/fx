package com.peebeekay.fx.simulation.data;

import java.util.Calendar;
import java.util.HashMap;

public interface IDataSubscriber {
	
	void accept(Price prices);
	Boolean isReady();

}
