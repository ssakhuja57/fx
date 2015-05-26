package com.peebeekay.fx.simulation.monitors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.distributors.ADataDistributor;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.simulation.trades.Trade.Status;
import com.peebeekay.fx.utils.Logger;

public abstract class ATradeMonitor implements IDataSubscriber, Runnable{
	
	protected Trade trade;
	protected boolean isActive = true;
	private ArrayList<ADataDistributor> dataSources = new ArrayList<ADataDistributor>();;
	protected Queue<Tick> priceQueue = new LinkedList<Tick>();
	protected Status validStatus;
	
	public ATradeMonitor(Trade trade){
		this.trade = trade;
	}
	
	@Override
	public abstract void accept(Tick price);
	
	@Override
	public abstract void accept(OhlcPrice price);
	
	
	public void subscribeToDataDist(ADataDistributor dataSource){
		dataSource.subscribeTo(this);
		dataSources.add(dataSource);
	}
	
	public boolean checkValid(){
		if(!isActive){
			return false;
		}
		if(trade.getStatus().value > validStatus.value){
			this.cancel();
			return false;
		}
		if(trade.getStatus() == validStatus)
			return true;
		
		return false;
	}
	
	public abstract void execute(Tick price);
	
	public void cancel(){
		for(ADataDistributor d: dataSources){
			d.unsubscribe(this);
		}
		isActive = false;
	}


}
