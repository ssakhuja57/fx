package com.peebeekay.fx.trades;

import com.peebeekay.fx.info.Pair;

public interface ITradeInfoProvider {
	
	enum TradingStatus{
		WAITING, OPEN, NOT_FOUND;
	}
	
	public Trade getTrade(Pair pair) throws TradeNotFoundException;
	
	public Trade getTrade(String orderId) throws TradeNotFoundException;
	
	public Order getOrder(String orderId) throws TradeNotFoundException;
	
	public TradingStatus getTradingStatus(String orderId);
	
	
}
