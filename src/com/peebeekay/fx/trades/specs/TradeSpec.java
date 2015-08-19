package com.peebeekay.fx.trades.specs;

import java.util.Map;


abstract class TradeSpec{


	public enum TradeProperty{
	
		STOP_SIZE;
	
		private TradeProperty(){}
	}

	Map<TradeProperty, String> tradeProperties;


	public TradeSpec(Map<TradeProperty, String> tradeProperties){
			this.tradeProperties = tradeProperties;
	}

}