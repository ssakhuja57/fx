package com.peebeekay.fx.simulation.data.distributors;

import java.util.ArrayList;
import java.util.Calendar;

import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.data.types.Tick;

public abstract class ADataDistributor implements Runnable {
	
	protected IDataSource source;
	protected ArrayList<IDataSubscriber> subscribers;
	double publishingRate; //how quickly to push the data
	public enum DataType{
		TICK,M1,M5,M30
	}
	public ADataDistributor(IDataSource source)
	{
		this.source = source;
		subscribers = new ArrayList<IDataSubscriber>();
	}
	public void subscribeTo(IDataSubscriber subscriber) {
		subscribers.add(subscriber);
	}
	
	public void unsubscribe(IDataSubscriber subscriber){
		subscribers.remove(subscriber);
	}
	
	public abstract void updateCache();
	
	public abstract ArrayList<Tick>getHistorical(Calendar start, Calendar end, DataType type);
	public abstract void onTick();
	public abstract void onThirty();
	public abstract boolean stillHasData();

}
