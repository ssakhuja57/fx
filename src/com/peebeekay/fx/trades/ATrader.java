package com.peebeekay.fx.trades;

import java.util.ArrayList;
import java.util.List;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.trades.ITradeInfoProvider.TradingStatus;
import com.peebeekay.fx.trades.specs.CreateTradeSpec;

public abstract class ATrader implements IDataSubscriber {

	ITradeActionProvider tradeProvider;
	ITradeInfoProvider infoProvider;
	String name;
	
	ArrayList<String> orderIds = new ArrayList<String>();
	
	public ATrader(ITradeActionProvider tradeProvider, ITradeInfoProvider infoProvider, String name) {
		this.tradeProvider = tradeProvider;
		this.infoProvider = infoProvider;
		this.name = name;
	}
	
	protected void createOrder(CreateTradeSpec spec) throws OrderCreationException{
		if(okToCreateOrder()){
			String orderId = tradeProvider.createOrder(spec);
			orderIds.add(orderId);
		}
	}
	
	public List<Trade> getOpenTrades(){
		ArrayList<Trade> trades = new ArrayList<Trade>();
		for (String orderId: orderIds){
			try {
				trades.add(infoProvider.getTrade(orderId));
			} catch (TradeNotFoundException e) {}
		}
		return trades;
	}
	
	public int getNumberOfOpenTrades(Pair pair){
		int count = 0;
		for(String orderId: orderIds){
			if(infoProvider.getTradingStatus(orderId) == TradingStatus.OPEN){
				try {
					if(pair == infoProvider.getTrade(orderId).getPair()){
						count++;
					}
				} catch (TradeNotFoundException e) {}
			}
		}
		return count;
	}
	
	public int getNumberOfOpenTrades(){
		int count = 0;
		for(String orderId: orderIds){
			if(infoProvider.getTradingStatus(orderId) == TradingStatus.OPEN)
				count++;
		}
		return count;
	}
	

	public int getNumberOfWaitingOrders(Pair pair){
		int count = 0;
		for(String orderId: orderIds){
			if(infoProvider.getTradingStatus(orderId) == TradingStatus.WAITING){
				try {
					if(pair == infoProvider.getOrder(orderId).getPair()){
						count++;
					}
				} catch (TradeNotFoundException e) {}
			}
		}
		return count;
	}
	
	public void close(){
		
	}
	
	public abstract boolean okToCreateOrder();
	
	
	
}