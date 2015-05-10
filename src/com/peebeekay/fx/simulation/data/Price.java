package com.peebeekay.fx.simulation.data;

import java.util.Calendar;

public class Price {
	
	public double ask;
	public double bid;
	public Calendar tme;
	public Price(double bid, double ask, Calendar tme){
		this.bid = bid;
		this.ask = ask;
		this.tme = tme;
	}
}
