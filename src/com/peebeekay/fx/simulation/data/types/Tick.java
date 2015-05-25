package com.peebeekay.fx.simulation.data.types;

import java.util.Date;

import com.peebeekay.fx.info.Pair;

public class Tick {
	
	private Pair pair;
	private double ask;
	private double bid;
	private Date time;
	
	public Tick(Pair pair, double ask, double bid, Date time){
		this.pair = pair;
		this.ask = ask;
		this.bid = bid;
		this.time = time;
	}
	
	public Tick(double ask, double bid){
		this.ask = ask;
		this.bid = bid;
	}
	
	// getters
	public Pair getPair(){
		return pair;
	}
	public double getAsk(){
		return ask;
	}
	public double getBid(){
		return bid;
	}
	public Date getTime(){
		return time;
	}
	
	public double getEnterPrice(boolean isLong){
		if(isLong)
			return ask;
		return bid;
	}
	
	public double getExitPrice(boolean isLong){
		if(isLong)
			return bid;
		return ask;
	}
}
