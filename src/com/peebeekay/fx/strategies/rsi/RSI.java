package com.peebeekay.fx.strategies.rsi;

import java.util.ArrayList;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.indicator.IIndicator;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;

public class RSI implements IIndicator {
	
	private Interval interval;
	private int periods;
	private Boolean simple; //if true use simple moving average 
	private Boolean useBid; //
	private double rsi;
	
	private double averageGain = 0;
	private double averageLoss = 0;
	
	private OhlcPrice lastPrice;
	
	public RSI(Interval interval, int periods, Boolean simple, Boolean useBid, ArrayList<OhlcPrice> historicalPrices){
		this.interval = interval;
		 this.periods = periods;
		 this.simple = simple;
		 this.useBid = useBid;
		 
		 // trim off extra prices
		 while(historicalPrices.size() > periods+2){
			 if(historicalPrices.get(0).getInterval() != interval)
				throw new RuntimeException(interval + "data expected");
			 historicalPrices.remove(0);
		 }
		 
		 // trim off most recent price
		 historicalPrices.remove(historicalPrices.size()-1);
		 
//		 for(OhlcPrice p: historicalPrices)
//			 System.out.println(DateUtils.dateToString(p.getTime()) + "," + p.getBidClose());
		 
		 for(int i=1; i<periods+1; i++){
			 Logger.debug(historicalPrices.get(i).getBidClose() + " --- " + "bid close price on " + DateUtils.dateToString(historicalPrices.get(i).getTime()) + " was " + 
					 " for " + historicalPrices.get(i).getPair());
			 double change = (useBid)? historicalPrices.get(i).getBidClose() - historicalPrices.get(i-1).getBidClose(): 
				historicalPrices.get(i).getAskClose() - historicalPrices.get(i-1).getAskClose();			 
//				Logger.debug(i + ": calculation for period " + prices.get(i-1).getTime().toString() + " to " + prices.get(i).getTime().toString() + ": " + change);
			if(change > 0)
				averageGain += change/periods;
			else
				averageLoss -= change/periods;
		}

		rsi = 100 - (100/(1+(averageGain/averageLoss)));
		
		lastPrice = historicalPrices.get(historicalPrices.size()-1);
		
		Logger.debug(rsi + " --- first rsi calc");
	}
	
	@Override
	public void addDataPoint(OhlcPrice p) {
//		Logger.debug("received " + p.getBidClose() + " at " + DateUtils.dateToString(p.getTime()));
		if(p.getInterval() != interval){
			throw new RuntimeException(interval + "data expected, got " + p.getInterval());
		}
		double change = (useBid)? p.getBidClose() - lastPrice.getBidClose() : p.getAskClose() - lastPrice.getAskClose();
		
		if(change > 0){
			averageGain = (averageGain*(periods-1) + change)/periods;
			averageLoss = (averageLoss*(periods-1) +	0	)/periods;
		}
		else {
			averageGain = (averageGain*(periods-1) +	0	)/periods;
			averageLoss = (averageLoss*(periods-1) + -change )/periods;
		}
		
		rsi = 100 - (100/(1+(averageGain/averageLoss)));
		lastPrice = p;
		
//		Logger.debug(p.getTime().toString() + " : " + rsi + " (" + p.getBidClose() + ")");
		
//		Logger.debug(averageGain + " : " + averageLoss);
	}
	
	@Override
	public void addDataPoint(Tick price) {
		
	}
	

	@Override
	public double getValue() {
		return rsi;
	}


}
