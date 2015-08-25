package com.peebeekay.fx.trades;

import java.io.IOException;
import java.util.ArrayList;

import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.trades.ITradeInfoProvider.OrderStatus;
import com.peebeekay.fx.trades.ITradeInfoProvider.TradeStatus;
import com.peebeekay.fx.trades.specs.CreateTradeSpec;

public abstract class ATrader implements IDataSubscriber {

	ITradeActionProvider tradeProvider;
	ITradeInfoProvider infoProvider;
	String name;
	
	ArrayList<Order> orders = new ArrayList<Order>();
	ArrayList<Trade> trades = new ArrayList<Trade>();
	
	public ATrader(ITradeActionProvider tradeProvider, ITradeInfoProvider infoProvider, String name) {
		this.tradeProvider = tradeProvider;
		this.infoProvider = infoProvider;
		this.name = name;
	}
	
	protected void createOrder(CreateTradeSpec spec) throws OrderCreationException{
		if(okToCreateOrder()){
			Order o = tradeProvider.createOrder(spec);
			orders.add(o);
		}
	}
	
	public int getNumberOfOpenTrades(){
		int count = 0;
		for(Trade trade: trades){
			if(infoProvider.getTradeStatus(trade) == TradeStatus.OPEN)
				count++;
		}
		return count;
	}
	
	public int getNumberOfWaitingOrders(){
		int count = 0;
		for(Order order: orders){
			if(infoProvider.getOrderStatus(order) == OrderStatus.WAITING)
				count++;
		}
		return count;
	}
	
	public void close() throws IOException{
		
	}
	
	public abstract boolean okToCreateOrder();
	
	
	
}