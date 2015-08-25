package com.peebeekay.fx.trades;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.trades.Trade;
import com.peebeekay.fx.utils.RateUtils;

public class StopAdjuster implements ITradeMonitor{
	
	int maxStopSize;
	int initialStopSize;
	
	Order order;
	Trade trade;
	ITradeInfoProvider info;
	
	boolean isLong;
	
	Tick lastPrice;
	int currentStopSize;
	
	
	public StopAdjuster(ITradeInfoProvider info, Trade trade, int maxStopSize, int initialStopSize){
		this.info = info;
		this.trade = trade;
		this.maxStopSize = maxStopSize;
		this.initialStopSize = initialStopSize;
		
		isLong = trade.getIsLong();
	}

	@Override
	public void accept(OhlcPrice price) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(Tick price) {
		if(lastPrice != null){
			if(RateUtils.isEqualOrBetter(price, lastPrice, isLong, false)){
				currentStopSize = info.getStopSize(trade);
				if(currentStopSize > maxStopSize){
					int sign = isLong ? -1 : 1;
					double newStopPrice = RateUtils.addPips(price.getExitPrice(isLong), sign*maxStopSize);
				}
			}
		}
		
		lastPrice = price;
	}

	
}
