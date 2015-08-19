package com.peebeekay.fx.trades.specs;

import java.util.Map;

import com.peebeekay.fx.info.Pair;



public class CreateTradeSpec extends TradeSpec{


	public enum OpenTradeType{
		MARKET_OPEN, MARKET_RANGE_OPEN, ORDER_ENTRY;
		private OpenTradeType(){}
	}


	public enum CloseTradeType{
		STOP_CLOSE, LIMIT_CLOSE, NONE;
		private CloseTradeType(){}
	}


	private Pair pair;

	private int lots;

	private boolean isLong;
	
	public CreateTradeSpec(Pair pair, int lots, boolean isLong, 
			OpenTradeType openType, CloseTradeType closeType,
			Map<TradeProperty, String> tradeProperties){
		super(tradeProperties);
		this.pair = pair;
		this.lots = lots;
		this.isLong = isLong;
	}
	
	public Pair getPair(){
		return pair;
	}
	public int getLots(){
		return lots;
	}
	public boolean getIsLong(){
		return isLong;
	}


	
	

}
