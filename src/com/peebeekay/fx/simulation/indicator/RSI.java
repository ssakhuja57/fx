package com.peebeekay.fx.simulation.indicator;

import java.util.ArrayList;

import com.peebeekay.fx.simulation.data.Price;

public class RSI implements IIndicator {
	
	private int period;
	private ArrayList<Price>prices;
	Boolean simple; //if true use simple moving average 
	private Boolean useBid; //
	public RSI(int period, Boolean simple, Boolean useBid, ArrayList<Price> prices){
		 this.period = period;
		 this.simple = simple;
		 this.useBid = useBid;
		 int i=0;
		 for(Price p: prices){
			 this.prices.add(i, p);
			 i++;
		 }
	}
	@Override
	public void addDataPoint(Price p) {
		prices.add(p);
		prices.remove(0);
	}

	@Override
	public double getValue() {
		if(prices.size() == period)
			return calcRSI();
		else
			return -1;
	}
	
	private double calcRSI(){
		double averageUp =0;
		double averageDown=0;
		for(int i =1; i<period; i++){
			double change = (useBid)? prices.get(i).getBid() - prices.get(i-1).getBid(): prices.get(i).getAsk() - prices.get(i-1).getAsk();
			if(change > 0)
				averageUp += change; //simple average
			else
				averageDown += (-change);
			
		}
		double rsi = 100 - (100/(1+(averageUp/averageDown)));
		return rsi;
	}

}