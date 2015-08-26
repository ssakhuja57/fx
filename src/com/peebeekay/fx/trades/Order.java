package com.peebeekay.fx.trades;

import com.peebeekay.fx.info.Pair;

public class Order {

	private String id;
	private Pair pair;
	private boolean isLong;
	
	// can be modified by trade table listener
	private int lots;
	private double stopPrice;
	
	public Order(String id, Pair pair, boolean isLong, int lots, double stopPrice){
		this.id = id;
		this.pair = pair;
		this.isLong = isLong;
		this.lots = lots;
		this.stopPrice = stopPrice;
	}
	
	public String getId(){
		return id;
	}
	public Pair getPair(){
		return pair;
	}
	public boolean getIsLong(){
		return isLong;
	}
	public int getLots(){
		return lots;
	}
	public double getStopPrice(){
		return stopPrice;
	}
	
	public void updateLots(int lots){
		this.lots = lots;
	}
	public void updateStopPrice(double stopPrice){
		this.stopPrice = stopPrice;
	}
}
