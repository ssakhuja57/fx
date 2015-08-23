package com.peebeekay.fx.trades.specs;

import java.util.Map;


public abstract class TradeSpec{


	public enum TradeProperty{
	
		STOP_SIZE;
	
		private TradeProperty(){}
	}

	Map<TradeProperty, String> tradeProperties;


	public TradeSpec(Map<TradeProperty, String> tradeProperties){
			this.tradeProperties = tradeProperties;
	}
	
	public Map<TradeProperty, String> getTradeProperties(){
		return tradeProperties;
	}

}