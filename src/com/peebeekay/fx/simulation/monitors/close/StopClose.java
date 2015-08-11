package com.peebeekay.fx.simulation.monitors.close;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.ReferenceLine;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.RateUtils;

public class StopClose extends ACloseTradeMonitor{

	int maxOffset;
	boolean trail;
	ReferenceLine stopLine;
	boolean isLong;
	Tick lastPrice;
	int ticks = 0;
	
	
	public StopClose(Trade trade, int offset, boolean trail) {
		super(trade);
		this.maxOffset = offset;
		this.trail = trail;
		isLong = super.trade.getIsLong();
	}

//	@Override
//	public void accept(Tick price) {
//		if(!super.checkValid()){
//			return;
//		}
//		Tick lastPrice = super.priceQueue.peek();
//		super.priceQueue.add(price);
//		if(lastPrice == null){ // this means this monitor has just been initialized, set the initial stopLine
//			int sign = isLong ? -1 : 1;
//			double refPrice = isLong ? trade.getOpenTick().getBid() : trade.getOpenTick().getAsk();
//			double stopPrice = RateUtils.addPips(refPrice, sign*maxOffset);
//			stopLine = new Tick(stopPrice, stopPrice);
//			return;
//		}
//
//		if(RateUtils.crosses(lastPrice, price, stopLine, isLong)){
//			super.execute(price);
//		}
//		
//		if(trail){
//			if(RateUtils.isBetter(price, lastPrice, isLong)){
//				currentOffset = RateUtils.getAbsPipDistance(price.getExitPrice(isLong), stopLine.getExitPrice(isLong));
//				if(currentOffset >= maxOffset){
//					int sign = isLong ? -1 : 1;
//					double newStopPrice = RateUtils.addPips(price.getExitPrice(isLong), sign*maxOffset);
//					stopLine = new Tick(newStopPrice, newStopPrice);
//				}
//			}
//		}	
//	}
	
	@Override
	public void accept(Tick price) {
		if(!super.checkValid()){
			return;
		}
		if(lastPrice == null){ // this means this monitor has just been initialized, set the initial stopLine
			int sign = isLong ? -1 : 1;
			double refPrice = price.getExitPrice(isLong);
			double stopPrice = RateUtils.addPips(refPrice, sign*maxOffset);
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
	public Boolean isReady() {
		return null;
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
