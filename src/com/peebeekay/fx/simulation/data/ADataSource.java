package com.peebeekay.fx.simulation.data;

import java.util.ArrayList;

public abstract class ADataSource {
	
	ArrayList<IDataSubscriber> subscribers;
	double publishingRate; //how quickly to push the data
	void subscribeTo(IDataSubscriber subscriber) {
		subscribers.add(subscriber);
	}
	

}
