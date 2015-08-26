package com.peebeekay.fx.trades;

public interface ITradeInfoProvider {
	
	enum TradingStatus{
		WAITING, OPEN, NOT_FOUND;
	}
	
	
	public Trade getTrade(String orderId) throws TradeNotFoundException;
	
	public Order getOrder(String orderId) throws TradeNotFoundException;
	
	public double getStopSize(String orderId) throws TradeNotFoundException;
	
	public TradingStatus getTradingStatus(String orderId);
	
}
