package com.peebeekay.fx.tools;

import java.util.Calendar;

public class DataRow implements Comparable<DataRow> {
	
	private Calendar time;
	private double bid;
	private double ask;
	
	
	public DataRow(Calendar time, double bid, double ask){
		this.time = time;
		this.bid = bid;
		this.ask = ask;
	}

	public Calendar getTime() {
		return time;
	}
	
	public void setOrdering(){
		
	}
	public void setTime(Calendar time) {
		this.time = time;
	}

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}

	public double getAsk() {
		return ask;
	}

	public void setAsk(double ask) {
		this.ask = ask;
	}

	@Override
	public int compareTo(DataRow ord) {
		if(this.time.before(ord.time))
		{
			return -1;
		}
		else if(this.time.after(ord.time))
		{
			return 1;
		}
		else return 0;
	}
	
	

}
