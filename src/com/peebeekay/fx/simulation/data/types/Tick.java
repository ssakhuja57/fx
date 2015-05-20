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
}
