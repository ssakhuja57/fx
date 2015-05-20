package com.peebeekay.fx.simulation.data.types;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;

public class OhlcPrice {
	
	private Pair pair;
	private Interval interval;
	
	private double askOpen;
	private double askHigh;
	private double askLow;
	private double askClose;
	
	private double bidOpen;
	private double bidHigh;
	private double bidLow;
	private double bidClose;
	
	public OhlcPrice(Pair pair, Interval interval, double askOpen, double askHigh, double askLow, double askClose,
			double bidOpen, double bidHigh, double bidLow, double bidClose){
		this.pair = pair;
		this.interval = interval;
		
		this.askOpen = askOpen;
		this.askHigh = askHigh;
		this.askLow = askLow;
		this.askClose = askClose;
		
		this.bidOpen = bidOpen;
		this.bidHigh = bidHigh;
		this.bidLow = bidLow;
		this.bidClose = bidClose;
	}
	
	public Pair getPair(){
		return pair;
	}
	public Interval getInterval(){
		return interval;
	}
	
	public double getAskOpen(){
		return askOpen;
	}
	public double getAskHigh(){
		return askHigh;
	}
	public double getAskLow(){
		return askLow;
	}
	public double getAskClose(){
		return askClose;
	}
	
	public double getBidOpen(){
		return bidOpen;
	}
	public double getBidHigh(){
		return bidHigh;
	}
	public double getBidLow(){
		return bidLow;
	}
	public double getBidClose(){
		return bidClose;
	}
	
	
	
	
}
