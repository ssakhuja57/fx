package com.peebeekay.fx.rates;

import java.util.LinkedList;
import java.util.Queue;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.OhlcPrice.AskBid;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.utils.Logger;

public class RateStats implements IDataSubscriber{

	private LinkedList<OhlcPrice> prices = new LinkedList<OhlcPrice>();
	private LinkedList<Tick> tickPrices = new LinkedList<Tick>();
	private int maxPrices;
	private Interval interval;
	
	public RateStats(int maxPrices, Interval interval){
		this.maxPrices = maxPrices;
		this.interval = interval;
	}


	@Override
	public void accept(OhlcPrice price) {
		if(prices.size() == maxPrices)
			prices.remove();
		prices.add(price);
	}

	@Override
	public void accept(Tick price) {
		
	}

	public double getExtreme(boolean isHigh, boolean isAsk){
		OhlcPrice price = prices.peek();
		AskBid p = price.getAskBid(isAsk);
		double extreme = isHigh ? p.getHigh() : p.getLow();

		for(OhlcPrice price_iter: prices){
			AskBid p_iter = price_iter.getAskBid(isAsk);
			if(isHigh){
				if(p_iter.getHigh() > extreme)
					extreme = p_iter.getHigh();
			}
			else{
				if(p_iter.getLow() < extreme)
					extreme = p_iter.getLow();
			}		
		}
		
		return extreme;
	}
	
	public double getRecentExtremum(int lastN, int clusterSize, boolean isHigh, boolean isAsk){
		if(clusterSize < 3 && clusterSize%2==0)
			throw new RuntimeException("cluster size needs to be an odd number and at least 3");
		if(lastN < 1)
			throw new RuntimeException("lastN needs to be at least 1");
		
		int required = (int)(clusterSize/2);
		
		int extremumCount = 0;
		
		Double result = null;
		
		for(int i=prices.size()-1 - required; i>=required; i--){ // start from the end of the list minus the required
			AskBid p = prices.get(i).getAskBid(isAsk);
			double p_val = isHigh ? p.getHigh() : p.getLow();
			boolean pass = true;
			for(int j=i-required; j<i+required; j++){
				AskBid p_j = prices.get(j).getAskBid(isAsk);
				if(isHigh){
					if(p_j.getHigh() > p_val){
						pass = false;
						break;
					}
				}
				else{
					if(p_j.getLow() < p_val){
						pass = false;
						break;
					}
				}
			}
			
			//check if everything passed and p is an extrema
			if(!pass)
				continue;
			
			//check if extrema is more extreme than current one (or if this is the first one)
			if(result == null){
				result = p_val;
			}
			else if(isHigh){
				if(p_val > result)
					result = p_val;
			}
			else{
				if(p_val < result)
					result = p_val;
			}
			
			//check if we have seen the lastN extremum yet
			extremumCount++;
			if(extremumCount >= lastN)
				break;
		}
		
		// if no local extrema found, return absolute extrema
		if(result == null){
			Logger.debug("using abs extrema");
			return getExtreme(isHigh, isAsk);
		}
			
		return result;
	}
	
	

}
