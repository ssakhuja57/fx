package com.peebeekay.fx.simulation.data.sources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;

public class RandomPriceData implements IDataSource{
	
	Random rand;
	
	public RandomPriceData(){
		rand = new Random();
	}

	@Override
	public ArrayList<Tick> getTicks(Pair pair, Calendar start, Calendar end) {
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		while(!start.after(end)){
			ticks.add(getTick(pair, start.getTime()));
			int inc = (int) (1000*(rand.nextDouble()/2));
			start.add(Calendar.MILLISECOND, inc);
		}
		return ticks;
	}

	@Override
	public ArrayList<OhlcPrice> getOhlcPrices(Pair pair, Interval interval,
			Calendar start, Calendar end) {
		return null;
	}

	private Tick getTick(Pair pair, Date time){
		double bid = rand.nextDouble()*3;
		double ask = rand.nextDouble()*3;
		return new Tick(pair, ask, bid, time);
	}
	
}
