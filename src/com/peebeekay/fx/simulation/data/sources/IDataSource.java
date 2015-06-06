package com.peebeekay.fx.simulation.data.sources;

import java.util.ArrayList;
import java.util.Calendar;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;

public interface IDataSource {
	
	public Tick getTickRow(int rowNum);
	
	public ArrayList<Tick> getTicks(Pair pair, Calendar start, Calendar end);
	
	public OhlcPrice getOhlcPrice(Pair pair, Interval interval, Calendar time);
	
	public ArrayList<OhlcPrice> getOhlcPrices(Pair pair, Interval interval, Calendar start, Calendar end);
}
