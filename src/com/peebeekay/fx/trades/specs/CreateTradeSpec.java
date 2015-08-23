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
	
	private OpenTradeType openType;
	
	private CloseTradeType closeType;
	
	public CreateTradeSpec(Pair pair, int lots, boolean isLong, 
			OpenTradeType openType, CloseTradeType closeType,
			Map<TradeProperty, String> tradeProperties){
		super(tradeProperties);
		this.pair = pair;
		this.lots = lots;
		this.isLong = isLong;
		this.openType = openType;
		this.closeType = closeType;
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
	public OpenTradeType getOpenType(){
		return openType;
	}
	public CloseTradeType getCloseType(){
		return closeType;
	}


	
	

}
