package com.peebeekay.fx.trades.specs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.peebeekay.fx.trades.specs.TradeSpec.TradeProperty;


public abstract class TradeSpec{


	public enum TradeProperty{
	
		STOP_SIZE;
	
		private TradeProperty(){}
	}

	Map<TradeProperty, String> tradeProperties;


	public TradeSpec(){
		tradeProperties = new HashMap<TradeProperty, String>();
	}
	
	public TradeSpec(Map<TradeProperty, String> tradeProperties){
			this.tradeProperties = tradeProperties;
	}
	
	public Map<TradeProperty, String> getTradeProperties(){
		return tradeProperties;
	}
	
	public String getTradeProperty(TradeProperty prop){
		return tradeProperties.get(prop);
	}
	
	public void setTradeProperty(TradeProperty prop, String value){
		tradeProperties.put(prop, value);
	}
	
	public abstract String toString();
	
	protected String getPropsString(){
		String res = "";
		for(Entry<TradeProperty, String> prop: tradeProperties.entrySet())
			res += "," + prop.getKey() + ":" + prop.getValue();
		return res;
	}

}