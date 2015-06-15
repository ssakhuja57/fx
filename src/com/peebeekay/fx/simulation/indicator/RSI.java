package com.peebeekay.fx.simulation.indicator;

import java.util.ArrayList;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.utils.Logger;

public class RSI implements IIndicator {
	
	private Interval interval;
	private int periods;
	private int arraySize;
	private ArrayList<OhlcPrice> prices;
	Boolean simple; //if true use simple moving average 
	private Boolean useBid; //
	
	private double averageGain = 0;
	private double averageLoss = 0;
	
	
	public RSI(Interval interval, int periods, Boolean simple, Boolean useBid, ArrayList<OhlcPrice> historicalPrices){
		this.interval = interval;
		 this.periods = periods;
		 arraySize = periods + 1;
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
		 while(prices.size() > arraySize){
			 prices.remove(0);
		 }
	}
	
	@Override
	public void addDataPoint(OhlcPrice p) {
//		Logger.debug("received " + p.getBidClose() + " at " + DateUtils.dateToString(p.getTime(), DateUtils.DATE_FORMAT_MILLI));
		if(p.getInterval() != interval){
			throw new RuntimeException(interval + "data expected, got " + p.getInterval());
		}
		prices.add(p);
		prices.remove(0);
	}
	
	@Override
	public void addDataPoint(Tick price) {
		
	}
	

	@Override
	public double getValue() {
		if(prices.size() == arraySize)
			return calcRSI();
		else
			throw new RuntimeException(arraySize + " values needed in RSI, only have " + prices.size());
	}
	
	private double calcRSI(){
		double averageGainCurr =0;
		double averageLossCurr=0;
		for(int i=1; i<arraySize; i++){
			double change = (useBid)? prices.get(i).getBidClose() - prices.get(i-1).getBidClose(): prices.get(i).getAskClose() - prices.get(i-1).getAskClose();
//			Logger.debug(i + ": calculation for period " + prices.get(i-1).getTime().toString() + " to " + prices.get(i).getTime().toString() + ": " + change);
			if(change > 0)
				averageGainCurr += change;
			else
				averageLossCurr -= change;
		}
		double rsi;
		
		if(averageGain == 0 && averageLoss == 0){
			averageGain = averageGainCurr;
			averageLoss = averageLossCurr;
		}
		else{
			averageGain = (averageGain*(periods - 1) + averageGainCurr)/periods;
			averageLoss = (averageLoss*(periods - 1) + averageLossCurr)/periods;
		}

		rsi = 100 - (100/(1+(averageGain/averageLoss)));
		
//		Logger.debug(rsi + "");
		return rsi;
	}


}
