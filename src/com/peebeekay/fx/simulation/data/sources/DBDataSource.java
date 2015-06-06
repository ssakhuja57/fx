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
	
	
	public DBDataSource(DBConfig config, Pair pair, Calendar start, Calendar end, boolean cacheAll){
		this.config = config;
		this.pair = pair;
		this.start = start;
		this.end = end;
		this.cacheAll = cacheAll;
		if(cacheAll){
			String sql = "select " + StringUtils.arrayToString(Tick.FIELDS, ",") + " from data.tick where ts >= '" + DateUtils.calToString(start) + "'"
					+ " AND ts <= '" + DateUtils.calToString(end) + "'"
					+ " AND pair = '" + pair + "'"
					+ " ORDER BY ts"
					+ ";";
			try {
				Logger.debug("initializing db data cache");
				tickCache.addAll(DBUtils.readQuery(config, sql));
				Logger.debug("db data cache initialized with " + tickCache.size() + " tick rows");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public Tick getTickRow(int rowNum){
		return Tick.arrayToTick(tickCache.get(rowNum));
	}

	@Override
	public ArrayList<Tick> getTicks(Pair pair, Calendar start, Calendar end) {
		return null;
	}

	@Override
	public ArrayList<OhlcPrice> getOhlcPrices(Pair pair, Interval interval,
			Calendar start, Calendar end) {
		String sql = "select " + StringUtils.arrayToString(OhlcPrice.FIELDS,",") + " from data." + interval.value 
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
			res.add(OhlcPrice.arrayToOhlc(row));
		}
		
		return res;	
	}
	


	@Override
	public OhlcPrice getOhlcPrice(Pair pair, Interval interval,
			Calendar time) {
		ArrayList<String[]> rows = new ArrayList<String[]>();
		String sql = "select " + StringUtils.arrayToString(OhlcPrice.FIELDS,",") + " from data." + interval.value 
				+ " WHERE ts = '" + DateUtils.calToString(time) + "'"
				+ " AND pair = '" + pair + "'"
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
