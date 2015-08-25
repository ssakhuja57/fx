package com.peebeekay.fx.trades;

import com.peebeekay.fx.info.Pair;

public interface ITradeInfoProvider {
	
	enum TradeStatus{
		OPEN, NOT_OPEN;
	}
	
	enum OrderStatus{
		WAITING, NOT_WAITING;
	}
	
	public Order getOrder(String orderId);

	public Order getOrder(Pair pair, boolean isLong);
	
	
	
	public Trade getTradeFromTradeId(String tradeId);

	public Trade getTradeFromOrderId(String orderId);
	
	public Trade getTrade(Pair pair);
	
	
	
	public int getStopSize(Trade trade);
	
	public int getStopSize(Order order);
	
	
	public TradeStatus getTradeStatus(Trade trade);
	
	public OrderStatus getOrderStatus(Order order);
}
