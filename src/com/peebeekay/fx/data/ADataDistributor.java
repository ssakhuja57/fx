package com.peebeekay.fx.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.IDataSubscriber;

public abstract class ADataDistributor{
	
	protected ArrayList<IDataSubscriber> subscribers = new ArrayList<IDataSubscriber>();
	protected Map<IDataSubscriber, List<Pair>> subscriberPairs = new HashMap<IDataSubscriber, List<Pair>>();
	protected Map<IDataSubscriber, List<Interval>> subscriberIntervals = new HashMap<IDataSubscriber, List<Interval>>();
	
	
	public void addSubscriber(IDataSubscriber ds, List<Pair> pairs, List<Interval> intervals){
		subscribers.add(ds);
		subscriberPairs.put(ds, pairs);
		subscriberIntervals.put(ds, intervals);
	}
	
	public void close(){
		unsubscribeAll();
	}
	
	void unsubscribeAll(){
		subscribers.clear();
	}

}
