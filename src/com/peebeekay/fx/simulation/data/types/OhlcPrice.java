package com.peebeekay.fx.simulation.data.types;

import java.text.ParseException;
import java.util.Date;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.StringUtils;

public class OhlcPrice {
	
	private Pair pair;
	private Date time;
	private Interval interval;
	
	private double askOpen;
	private double askHigh;
	private double askLow;
	private double askClose;
	
	private double bidOpen;
	private double bidHigh;
	private double bidLow;
	private double bidClose;
	
	public static final String[] FIELDS = new String[] {"pair","interval","ts","askOpen","askHigh","askLow","askClose","bidOpen","bidHigh","bidLow","bidClose"};
	
	public OhlcPrice(Pair pair, Date time, Interval interval, double askOpen, double askHigh, double askLow, double askClose,
			double bidOpen, double bidHigh, double bidLow, double bidClose){
		this.pair = pair;
		this.time = time;
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
	
	public static OhlcPrice arrayToOhlc(String[] row){
		final int expected = FIELDS.length;
		if(row.length != expected){
			throw new RuntimeException("expected " + expected + " values for OHLC, got " + row.length);
		}
		Pair pair = StringUtils.getEnumFromString(Pair.class, row[0]);
		Interval interval = Interval.valueOf(row[1].toUpperCase());
		Date date = null;
		try {
			date = DateUtils.parseDate(row[2], DateUtils.DATE_FORMAT_STD);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("date parse error: " + row[2]);
		}
		double askOpen = Double.parseDouble(row[3]);
		double askHigh = Double.parseDouble(row[4]);
		double askLow = Double.parseDouble(row[5]);
		double askClose = Double.parseDouble(row[6]);
		
		double bidOpen = Double.parseDouble(row[7]);
		double bidHigh = Double.parseDouble(row[8]);
		double bidLow = Double.parseDouble(row[9]);
		double bidClose = Double.parseDouble(row[10]);
		
		return new OhlcPrice(pair, date, interval, askOpen, askHigh, askLow, askClose, bidOpen, bidHigh, bidLow, bidClose);
	}
	
	public Pair getPair(){
		return pair;
	}
	public Date getTime(){
		return time;
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
