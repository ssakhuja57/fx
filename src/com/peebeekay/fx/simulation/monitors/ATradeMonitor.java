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
	public void accept(Tick price){
		priceQueue.add(price);
	}
	
	public boolean checkIsActive(){
		return isActive;
	}
	
	public void subscribeToDataDist(ADataDistributor dataSource){
		dataSource.subscribeTo(this);
		dataSources.add(dataSource);
	}
	
	public boolean checkValidStatus(){
		if(!isActive){
			return false;
		}
		if(trade.getStatus().value > validStatus.value){
			this.cancel();
			return false;
		}
		return true;
	}
	
	public abstract void execute(Tick price);
	
	public void cancel(){
		for(ADataDistributor d: dataSources){
			d.unsubscribe(this);
		}
		isActive = false;
	}


}
