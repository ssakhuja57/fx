package com.peebeekay.fx.data;

import java.util.ArrayList;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.simulation.data.IDataSubscriber;

public abstract class ADataDistributor{
	
	protected ArrayList<IDataSubscriber> subscribers = new ArrayList<IDataSubscriber>();
	
	
	public void addSubscriber(IDataSubscriber ds){
		subscribers.add(ds);
	}
	
	public void close(){
		unsubscribeAll();
	}
	
	void unsubscribeAll(){
		subscribers.clear();
	}

}
