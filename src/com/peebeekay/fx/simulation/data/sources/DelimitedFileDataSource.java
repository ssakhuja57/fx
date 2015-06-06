package com.peebeekay.fx.simulation.data.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.utils.FileUtils;
import com.peebeekay.fx.utils.Logger;

public class DelimitedFileDataSource implements IDataSource{
	
	private File tickFile;
	private Map<Interval, File> ohlcFiles;
	private String delimiter;
	private Pair pair;
	private Calendar start;
	private Calendar end;
	private boolean cacheAll;
	
	private ArrayList<Tick> tickCache = new ArrayList<Tick>();
	
	
	public DelimitedFileDataSource(String tickFile, Map<Interval,File> ohlcFiles, String delimiter, Pair pair, Calendar start, Calendar end, boolean cacheAll) {
		this.tickFile = new File(tickFile);
		this.ohlcFiles = ohlcFiles;
		this.delimiter = delimiter;
		this.pair = pair;
		this.start = start;
		this.end = end;
		this.cacheAll = cacheAll;
		
		if(cacheAll){
			BufferedReader br = null;
			try {
				br = FileUtils.readFile(new File(tickFile));
				String line;
				while ((line = br.readLine()) != null){
					String[] row = line.split(delimiter);
					//Logger.debug(row[0]);
//					if(row[0].trim() != pair.name())
//						continue;
					if(row.length != Tick.FIELDS.length)
						throw new RuntimeException("file data source row doesn't have the number of columns as expected");
					tickCache.add(Tick.arrayToTick(row));
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			} finally{
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Logger.debug("tick cache initialized with " + tickCache.size() + " ticks");
		}
	}
	
	@Override
	public Tick getTickRow(int rowNum) {
		return tickCache.get(rowNum);
	}

	@Override
	public ArrayList<Tick> getTicks(Pair pair, Calendar start, Calendar end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<OhlcPrice> getOhlcPrices(Pair pair, Interval interval,
			Calendar start, Calendar end) {
		ArrayList<OhlcPrice> prices = new ArrayList<OhlcPrice>();
		BufferedReader br;
		try {
			br = FileUtils.readFile(ohlcFiles.get(interval));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		String line;
		try {
			while ((line = br.readLine()) != null){
				OhlcPrice price = OhlcPrice.arrayToOhlc( line.split(delimiter));
				if( pair == price.getPair() && 
						(!price.getTime().before(start.getTime()) && !price.getTime().after(end.getTime())))
					prices.add(price);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return prices;
	}

	@Override
	public OhlcPrice getOhlcPrice(Pair pair, Interval interval, Calendar time) {
		ArrayList<OhlcPrice> prices = new ArrayList<OhlcPrice>();
		BufferedReader br;
		try {
			br = FileUtils.readFile(ohlcFiles.get(interval));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		String line;
		try {
			while ((line = br.readLine()) != null){
				OhlcPrice price = OhlcPrice.arrayToOhlc( line.split(delimiter) );
				if(pair == price.getPair() && price.getTime().equals(time.getTime()) )
					prices.add(price);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		if(prices.size() != 1)
			return null;
		return prices.get(0);
	}

	
}
