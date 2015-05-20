package com.peebeekay.fx.simulation.monitors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.distributors.ADataDistributor;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.simulation.trades.Trade.Status;

public abstract class ATradeMonitor implements IDataSubscriber, Runnable{
	
	protected Trade trade;
	protected boolean isActive;
	private ArrayList<ADataDistributor> dataSources = new ArrayList<ADataDistributor>();;
	protected Queue<Tick> priceQueue = new LinkedList<Tick>();
	protected List<Status> validStatuses = new LinkedList<Status>();
	
	public ATradeMonitor(Trade trade){
		this.trade = trade;
	}
	
	public void acceptData(Tick price){
		priceQueue.add(price);
	}
	
	public void subscribeToDataDist(ADataDistributor dataSource){
		dataSource.subscribeTo(this);
		dataSources.add(dataSource);
	}
	
	public boolean checkValidStatus(){
		return validStatuses.contains(trade.getStatus());
	}
	
	public abstract void execute(Tick price);
	
	public void activate(){
		isActive = true;
	}
	
	public void cancel(){
		for(ADataDistributor d: dataSources){
			d.unsubscribe(this);
		}
		isActive = false;
	}


}
