package com.peebeekay.fx.simulation.data;

import java.util.Date;

public class Price {
	
	private double ask;
	private double bid;
	private Date time;
	public Price(double bid, double ask, Date time){
		this.bid = bid;
		this.ask = ask;
		this.time = time;
	}
	
	// getters
	public double getAsk(){
		return ask;
	}
	public double getBid(){
		return bid;
	}
	public Date getTime(){
		return time;
	}
}
