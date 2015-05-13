package com.peebeekay.fx.simulation.data;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class ADataSource {
	
	ArrayList<IDataSubscriber> subscribers;
	double publishingRate; //how quickly to push the data
	public enum DataType{
		TICK,M1,M5,M30
	}
	void subscribeTo(IDataSubscriber subscriber) {
		subscribers.add(subscriber);
	}
	
	public abstract ArrayList<Price>getHistorical(Calendar start, Calendar end, DataType type);

}