package com.peebeekay.fx.simulation.data.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;

public class DelimitedFileDataSource implements IDataSource{
	
	private File file;
	private String delimiter;
	private Pair pair;
	private Calendar start;
	private Calendar end;
	private boolean cacheAll;
	
	private ArrayList<Tick> tickCache = new ArrayList<Tick>();
	
	
	public DelimitedFileDataSource(String file, String delimiter, Pair pair, Calendar start, Calendar end, boolean cacheAll) {
		this.file = new File("file");
		this.delimiter = delimiter;
		this.pair = pair;
		this.start = start;
		this.end = end;
		this.cacheAll = cacheAll;
		
		if(cacheAll){
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null){
					String[] row = line.split(delimiter);
					if(row[0] != pair.toString())
						continue;
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
		// TODO Auto-generated method stub
		return null;
	}

	
}
