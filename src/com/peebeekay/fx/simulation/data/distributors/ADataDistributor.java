package com.peebeekay.fx.simulation.data.distributors;

import java.util.ArrayList;
import java.util.Calendar;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;

public abstract class ADataDistributor implements Runnable {

	protected Pair pair;
	protected Calendar start, end;
	protected IDataSource source;
	protected ArrayList<IDataSubscriber> subscribers;
	double publishingRate; // how quickly to push the data


	public ADataDistributor(IDataSource source, Pair pair, Calendar start, Calendar end) {
		this.pair = pair;
		this.start = start;
		this.end = end;
		this.source = source;
		subscribers = new ArrayList<IDataSubscriber>();
	}

	public void subscribeTo(IDataSubscriber subscriber) {
		subscribers.add(subscriber);
	}

	public void unsubscribe(IDataSubscriber subscriber) {
		subscribers.remove(subscriber);
	}

	public abstract void updateCache();

	public abstract ArrayList<Tick> getHistorical(Calendar start, Calendar end);
	
	public abstract ArrayList<OhlcPrice> getHistoricalOhlc(Calendar start, Calendar end, Interval type);


	public abstract boolean stillHasData();

	public abstract void onFire(Calendar currTime);
	

}
