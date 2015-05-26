package com.peebeekay.fx.simulation.indicator;

import java.util.ArrayList;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;

public class RSI implements IIndicator {
	
	private Interval interval;
	private int period;
	private ArrayList<OhlcPrice> prices;
	Boolean simple; //if true use simple moving average 
	private Boolean useBid; //
	public RSI(Interval interval, int period, Boolean simple, Boolean useBid, ArrayList<OhlcPrice> historicalPrices){
		this.interval = interval;
		 this.period = period;
		 this.simple = simple;
		 this.useBid = useBid;
		 this.prices = new ArrayList<OhlcPrice>();
		 
		 int i=0;
		 for(OhlcPrice p: historicalPrices){
			if(p.getInterval() != interval)
				throw new RuntimeException(interval + " data expected, got " + p.getInterval());
			 this.prices.add(i, p);
			 i++;
		 }
		 while(prices.size() > period){
			 prices.remove(0);
		 }
	}
	
	public Interval getInterval(){
		return interval;
	}

	public void addDataPoint(OhlcPrice p) {
		if(p.getInterval() != interval){
			throw new RuntimeException(interval + "data expected, got " + p.getInterval());
		}
		prices.add(p);
		prices.remove(0);
	}
	

	@Override
	public double getValue() {
		if(prices.size() == period)
			return calcRSI();
		else
			throw new RuntimeException(period + " values needed in RSI, only have " + prices.size());
	}
	
	private double calcRSI(){
		double averageUp =0;
		double averageDown=0;
		for(int i=1; i<period; i++){
			double change = (useBid)? prices.get(i).getBidClose() - prices.get(i-1).getBidClose(): prices.get(i).getAskClose() - prices.get(i-1).getAskClose();
			if(change > 0)
				averageUp += change; //simple average
			else
				averageDown += (-change);
			
		}
		double rsi = 100 - (100/(1+(averageUp/averageDown)));
		return rsi;
	}

}
