package com.peebeekay.fx.trades;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.trades.Trade;
import com.peebeekay.fx.trades.ITradeInfoProvider.TradingStatus;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.RateUtils;

public class StopAdjuster implements ITradeMonitor{
	
	int maxStopSize;
	int minStopSize;
	
	Order order;
	Trade trade;
	ITradeActionProvider ap;
	ITradeInfoProvider info;
	
	double openStopPrice;
	
	boolean isLong;
	
	Tick lastPrice;
	
	boolean active = true;
	
	
	public StopAdjuster(ITradeActionProvider ap, ITradeInfoProvider info, Trade trade, int minStopSize, int maxStopSize) throws TradeNotFoundException{
		this.ap = ap;
		this.info = info;
		this.trade = trade;
		this.minStopSize = minStopSize;
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
				
				boolean adjust = true;
				
				double distanceFromOpenStop = 0;

				if(openStopPrice == 0){
					Logger.debug("setting open stop price for stop adjuster at " + trade.getInitialStopPrice());
					this.openStopPrice = trade.getInitialStopPrice();
					adjust = false;
				}
				else{
					distanceFromOpenStop = RateUtils.getAbsPipDistanceDbl(openStopPrice, price.getExitPrice(isLong));
					if(distanceFromOpenStop >= maxStopSize
							|| info.getTradingStatus(trade.getId()) == TradingStatus.NOT_FOUND){
//						Logger.debug("open stop price: " + openStopPrice + " status: " + info.getTradingStatus(trade.getId()));
						Logger.debug("ending stop adjuster for " + trade.getPair() 
								+ " (stop size: " + distanceFromOpenStop + " status: " + info.getTradingStatus(trade.getId()) + ")");
						active = false; // stop is at max price, this stop adjuster's job is done
						adjust = false;
					}
					if(distanceFromOpenStop <= minStopSize)
						adjust = false;
				}
				
				
				if(!RateUtils.isEqualOrBetter(price, lastPrice, isLong, false))
					adjust = false;
				
				if(adjust){
					Logger.debug("adjusting stop for " + trade.getPair() + " to " + distanceFromOpenStop);
					ap.adjustStop(order, (int)distanceFromOpenStop);
				}
		}
		
		
		lastPrice = price;
	}

	
}
