package com.peebeekay.fx.simulation.data.types;

import java.util.Date;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.utils.DateUtils;

public class Tick {
	
	private Pair pair;
	private double ask;
	private double bid;
	private Date ts;
	public static final String[] FIELDS = new String[] {"pair", "ts", "ask", "bid"};
	
	public Tick(Pair pair, Date ts, double ask, double bid){
		this.pair = pair;
		this.ask = ask;
		this.bid = bid;
		this.ts = ts;
	}
	
	public static Tick arrayToTick(String[] values){
		if(values.length != FIELDS.length)
			throw new RuntimeException("number of values for tick was not what was expected");
		try {
			return new Tick(Pair.valueOf(values[0]), DateUtils.parseDate(values[1], DateUtils.DATE_FORMAT_MILLI), Double.parseDouble(values[2]), Double.parseDouble(values[3]));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
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
		return ts;
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
	
	public double getSpread(){
		return ask - bid;
	}
	
	@Override
	public String toString(){
		return DateUtils.dateToString(ts, DateUtils.DATE_FORMAT_MILLI)
				+ "," + pair + "," + ask + "," + bid;
	}
}
