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
	private boolean cache;
	private final int CACHE_DAYS = 20;
	private Calendar startChunk;
	private int cacheRow = 0;
	private ArrayList<String[]> tickCache = new ArrayList<String[]>();
	
	
	public DBDataSource(DBConfig config, Pair pair, Calendar start, Calendar end, boolean cache){
		this.config = config;
		this.pair = pair;
		this.start = start;
		this.startChunk = start;
		this.end = end;
		this.cache = cache;
		updateCache();
	}
	
	private String getQuery(Calendar startDate, Calendar endDate){

		return "SELECT " + StringUtils.arrayToString(Tick.FIELDS, ",")
				+ " FROM data.tick"
				+ " WHERE pair = '" + pair + "'"
				+ " AND ts >= '" + DateUtils.calToString(startDate, DateUtils.DATE_FORMAT_MILLI) + "'"
				+ " AND ts < '" + DateUtils.calToString(endDate, DateUtils.DATE_FORMAT_MILLI) + "'"
				+ " ORDER BY ts"
				;
	}
	
	private boolean updateCache(){
		if(startChunk.after(end) || startChunk.equals(end))
			return false;
		Calendar endChunk = Calendar.getInstance();
		endChunk.setTime(startChunk.getTime());
		endChunk.add(Calendar.DATE, CACHE_DAYS);
		if(endChunk.after(end))
			endChunk = end;
		Logger.debug("clearing tick cache");
		tickCache.clear();
		try {
			Logger.debug("updating cache with tick data from " 
							+ DateUtils.calToString(startChunk) + " to " + DateUtils.calToString(endChunk));
			ArrayList<String[]> data = DBUtils.readQuery(config, getQuery(startChunk, endChunk));
			Logger.info("adding " + data.size() + " rows to tick cache");
			tickCache.addAll(data);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		startChunk = endChunk;
		return true;
	}
	
	
	@Override
	public Tick getTickRow() throws EndOfTickDataException{
		if(cacheRow >= tickCache.size()){
			if(!updateCache())
				throw new EndOfTickDataException();
			cacheRow = 0;
		}
		return Tick.arrayToTick(tickCache.get(cacheRow++));
	}

	@Override
	public ArrayList<Tick> getTicks(Pair pair, Calendar start, Calendar end) {
		return null;
	}

	@Override
	public ArrayList<OhlcPrice> getOhlcPrices(Pair pair, Interval interval,
			Calendar start, Calendar end) {
		String sql = "SELECT " + StringUtils.arrayToString(OhlcPrice.FIELDS,",").replace("interval,", "'" + interval.value + "',")
				+ " FROM data." + interval.value 
				+ " WHERE ts >= '" + DateUtils.calToString(start) + "'"
				+ " AND ts <= '" + DateUtils.calToString(end) + "'"
				+ " AND pair like '%" + pair + "%'"
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
			res.add(OhlcPrice.arrayToOhlc(row));
		}
		
		return res;	
	}
	


	@Override
	public OhlcPrice getOhlcPrice(Pair pair, Interval interval,
			Calendar time) {
		ArrayList<String[]> rows = new ArrayList<String[]>();
		String sql = "SELECT " + StringUtils.arrayToString(OhlcPrice.FIELDS,",").replace("interval,", "'" + interval.value + "',") 
				+ " FROM data." + interval.value 
				+ " WHERE ts = '" + DateUtils.calToString(time) + "'"
				+ " AND pair like '%" + pair + "%'"
				+ " ORDER BY ts"
				+ ";";
		try {
			rows = DBUtils.readQuery(config, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(rows.size() != 1)
			return null;
		return OhlcPrice.arrayToOhlc(rows.get(0));
	}
	
	
}
