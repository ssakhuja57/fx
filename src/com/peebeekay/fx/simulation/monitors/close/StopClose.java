package com.peebeekay.fx.simulation.monitors.close;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.ReferenceLine;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.RateUtils;

public class StopClose extends ACloseTradeMonitor{

	int maxOffset;
	int initialOffset;
	boolean trail;
	ReferenceLine stopLine;
	boolean isLong;
	Tick lastPrice;
	
	
	public StopClose(Trade trade, int maxOffset, boolean trail) {
		super(trade);
		this.maxOffset = maxOffset;
		initialOffset = 0;
		this.trail = trail;
		isLong = super.trade.getIsLong();
	}
	
	public StopClose(Trade trade, int maxOffset, int initialOffset){
		super(trade);
		if(initialOffset > maxOffset)
			throw new RuntimeException("initial offset cannot be greater than max offset");
		this.maxOffset = maxOffset;
		this.initialOffset = initialOffset;
		trail = true;
		isLong = super.trade.getIsLong();
	}

	
	@Override
	public void accept(Tick price) {
		if(!super.checkValid()){
			return;
		}
		if(lastPrice == null){ // this means this monitor has just been initialized, set the initial stopLine
			int sign = isLong ? -1 : 1;
			double refPrice = price.getExitPrice(isLong);
			int offset = (initialOffset == 0) ? maxOffset : initialOffset;
			double stopPrice = RateUtils.addPips(refPrice, sign*offset);
			stopLine = new ReferenceLine(stopPrice);
			lastPrice = price;
			return;
		}
		

		if(!RateUtils.isEqualOrBetter(price, stopLine, isLong, false)){
			super.execute(price);
			return;
		}
		
		if(trail){
			if(RateUtils.isEqualOrBetter(price, lastPrice, isLong, false)){
				int currentOffset = RateUtils.getAbsPipDistance(price.getExitPrice(isLong), stopLine.getValue());
				if(currentOffset > maxOffset){
					int sign = isLong ? -1 : 1;
					double newStopPrice = RateUtils.addPips(price.getExitPrice(isLong), sign*maxOffset);
					stopLine.adjustValue(newStopPrice);
				}
			}
		}
		
		lastPrice = price;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(OhlcPrice price) {
		// TODO Auto-generated method stub
		
	}

	
}
