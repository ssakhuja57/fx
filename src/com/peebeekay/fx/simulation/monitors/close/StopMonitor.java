package com.peebeekay.fx.simulation.monitors.close;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.simulation.trades.Trade.Status;
import com.peebeekay.fx.utils.RateUtils;

public class StopMonitor extends ACloseTradeMonitor{

	int maxOffset;
	int currentOffset;
	boolean trail;
	Tick stopLine;
	boolean isLong;
	
	
	public StopMonitor(Trade trade, int offset, boolean trail) {
		super(trade);
		this.maxOffset = offset;
		this.currentOffset = offset;
		this.trail = trail;
		isLong = super.trade.getIsLong();
	}

	@Override
	public void accept(Tick price) {
		if(!super.checkValidStatus()){
			return;
		}
		if(super.trade.getStatus() != Status.OPEN){
			return;
		}
		Tick lastPrice = super.priceQueue.peek();
		super.priceQueue.add(price);
		if(lastPrice == null){ // this means this monitor has just been initialized, set the initial stopLine
			int sign = isLong ? -1 : 1;
			double refPrice = isLong ? trade.getOpenTick().getBid() : trade.getOpenTick().getAsk();
			double stopPrice = RateUtils.addPips(refPrice, sign*maxOffset);
			stopLine = new Tick(stopPrice, stopPrice);
			return;
		}

		if(RateUtils.crosses(lastPrice, price, stopLine, isLong)){
			super.execute(price);
		}
		
		if(trail){
			if(RateUtils.isBetter(price, lastPrice, isLong)){
				currentOffset = RateUtils.getAbsPipDistance(price.getExitPrice(isLong), stopLine.getExitPrice(isLong));
				if(currentOffset >= maxOffset){
					int sign = isLong ? -1 : 1;
					double newStopPrice = RateUtils.addPips(price.getExitPrice(isLong), sign*maxOffset);
					stopLine = new Tick(newStopPrice, newStopPrice);
				}
			}
		}
		
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
