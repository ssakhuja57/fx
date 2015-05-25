package com.peebeekay.fx.simulation.data.sources;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.utils.DBUtils;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.StringUtils;
import com.peebeekay.fx.utils.config.DBConfig;

public class DBDataSource implements IDataSource{
	
	private DBConfig config;
	private Pair pair;
	private Calendar start;
	private Calendar end;
	private boolean cacheAll;
	private ArrayList<String[]> tickCache = new ArrayList<String[]>();
	
	private static final String TICK_FIELDS = "pair,ts,ask,bid";
	private static final String OHLC_FIELDS = "pair,ts,askOpen,askHigh,askLow,askClose,bidOpen,bidHigh,bidLow,bidClose";
	
	public DBDataSource(DBConfig config, Pair pair, Calendar start, Calendar end, boolean cacheAll){
		this.config = config;
		this.pair = pair;
		this.start = start;
		this.end = end;
		this.cacheAll = cacheAll;
		if(cacheAll){
			String sql = "select " + TICK_FIELDS + " from data.tick where ts >= '" + DateUtils.calToString(start) + "'"
					+ " AND ts <= '" + DateUtils.calToString(end) + "'"
					+ " AND pair = '" + pair + "'"
					+ " ORDER BY ts"
					+ ";";
			try {
				tickCache.addAll(DBUtils.readQuery(config, sql));
				Logger.debug("db data cache initialized with " + tickCache.size() + " tick rows");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Tick arrayToTick(String[] row){
		final int expected = 4;
		if(row.length != expected){
			throw new RuntimeException("expected " + expected + " values for tick, got " + row.length);
		}
		Pair pair = StringUtils.getEnumFromString(Pair.class, row[0]);
		Date date = null;
		try {
			date = DateUtils.parseDate(row[1], DateUtils.DATE_FORMAT_MILLI);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("date parse error: " + row[1]);
		}
		double ask = Double.parseDouble(row[2]);
		double bid = Double.parseDouble(row[3]);
		return new Tick(pair, ask, bid, date);
	}
	
	private OhlcPrice arrayToOhlc(String[] row, Interval interval){
		final int expected = 11;
		if(row.length != expected){
			throw new RuntimeException("expected " + expected + " values for OHLC, got " + row.length);
		}
		Pair pair = StringUtils.getEnumFromString(Pair.class, row[0]);
		Date date = null;
		try {
			date = DateUtils.parseDate(row[1], DateUtils.DATE_FORMAT_STD);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("date parse error: " + row[1]);
		}
		double askOpen = Double.parseDouble(row[2]);
		double askHigh = Double.parseDouble(row[3]);
		double askLow = Double.parseDouble(row[4]);
		double askClose = Double.parseDouble(row[5]);
		
		double bidOpen = Double.parseDouble(row[6]);
		double bidHigh = Double.parseDouble(row[7]);
		double bidLow = Double.parseDouble(row[8]);
		double bidClose = Double.parseDouble(row[9]);
		
		return new OhlcPrice(pair, date, interval, askOpen, askHigh, askLow, askClose, bidOpen, bidHigh, bidLow, bidClose);
	}
	
	public Tick getTickRow(int rowNum){
		return arrayToTick(tickCache.get(rowNum));
	}

	@Override
	public ArrayList<Tick> getTicks(Pair pair, Calendar start, Calendar end) {
		return null;
	}

	@Override
	public ArrayList<OhlcPrice> getOhlcPrices(Pair pair, Interval interval,
			Calendar start, Calendar end) {
		String sql = "select " + OHLC_FIELDS + " from data." + interval.value 
				+ " WHERE ts >= '" + DateUtils.calToString(start) + "'"
				+ " AND ts <= '" + DateUtils.calToString(end) + "'"
				+ " AND pair = '" + pair + "'"
				+ " ORDER BY ts"
				+ ";";
		ArrayList<String[]> rows = null;
		try {
			rows = DBUtils.readQuery(config, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ArrayList<OhlcPrice> res = new ArrayList<OhlcPrice>();
		for(String[] row: rows){
			res.add(arrayToOhlc(row, interval));
		}
		
		return res;
		
	}
	
	
}
