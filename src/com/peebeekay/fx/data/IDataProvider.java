package com.peebeekay.fx.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;


public interface IDataProvider{


	public Tick getTick(Pair p);


	public OhlcPrice getOhlcRow(Pair p, Interval i);


	public OhlcPrice getOhlcRow(Pair p, Interval i, Calendar d);


	public ArrayList<Tick> getTicks(Pair p, Calendar start, Calendar end);


	public ArrayList<OhlcPrice> getOhlcRows(Pair p, Interval i, Calendar start, Calendar end);
	
	public List<Pair> getSubscribedPairs();



	}
