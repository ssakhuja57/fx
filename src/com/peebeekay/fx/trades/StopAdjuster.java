package com.peebeekay.fx.trades;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.trades.Trade;
import com.peebeekay.fx.trades.ITradeInfoProvider.TradingStatus;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.RateUtils;

public class StopAdjuster implements ITradeMonitor{
	
	int maxStopSize;
	int initialStopSize;
	
	Order order;
	Trade trade;
	ITradeActionProvider ap;
	ITradeInfoProvider info;
	
	double openPrice;
	double openStopPrice;
	
	boolean isLong;
	
	Tick lastPrice;
	
	boolean active = true;
	
	
	public StopAdjuster(ITradeActionProvider ap, ITradeInfoProvider info, Trade trade, int initialStopSize, int maxStopSize) throws TradeNotFoundException{
		this.ap = ap;
		this.info = info;
		this.trade = trade;
		this.openPrice = trade.getOpenPrice();
		this.openStopPrice = trade.getInitialStopPrice();
		this.initialStopSize = initialStopSize;
		this.maxStopSize = maxStopSize;
		
		isLong = trade.getIsLong();
		
		order = info.getOrder(trade.getPair());
	}

	@Override
	public void accept(OhlcPrice price) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(Tick price) {
		if(!active)
			return;
		if(lastPrice != null){
			if(RateUtils.isEqualOrBetter(price, lastPrice, isLong, false)){
				
				boolean adjust = true;
				
				double distanceFromOpenStop = RateUtils.getAbsPipDistanceDbl(openStopPrice, price.getExitPrice(isLong));
//				Logger.debug("init:" + initialStopSize + ", cur:" + distanceFromOpenStop + ", max: " + maxStopSize);
				
				if(distanceFromOpenStop >= maxStopSize
						|| info.getTradingStatus(trade.getId()) == TradingStatus.NOT_FOUND){
					active = false; // stop is at max price, this stop adjuster's job is done
					adjust = false;
				}
				if(distanceFromOpenStop <= initialStopSize)
					adjust = false;
				
				double change = price.getExitPrice(isLong) - lastPrice.getExitPrice(isLong);
				int sign = isLong ? 1 : -1;
				if(sign*change <= 0)
					adjust = false;
				
				if(adjust){
					ap.adjustStop(order, openStopPrice);
				}
			}
		}
		
		
		lastPrice = price;
	}

	
}
