package com.peebeekay.fx.simulation.monitors.open;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.utils.RateUtils;

public class EntryOpen extends AOpenTradeMonitor{

	private Tick entryPrice;
	
	public EntryOpen(Trade trade, Tick entryPrice) {
		super(trade);
		this.entryPrice = entryPrice;
	}

	@Override
	public Boolean isReady() {
		return true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(Tick price) {
		if(!super.checkValid())
			return;
		if(RateUtils.isBetter(price, entryPrice, super.trade.getIsLong())){
			super.execute(price);
		}
	}

	@Override
	public void accept(OhlcPrice price) {
		// TODO Auto-generated method stub
		
	}
	
	

}
