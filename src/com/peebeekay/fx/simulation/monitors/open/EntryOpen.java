package com.peebeekay.fx.simulation.monitors.open;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.ReferenceLine;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.utils.RateUtils;

public class EntryOpen extends AOpenTradeMonitor{

	private ReferenceLine entryLine;
	
	public EntryOpen(Trade trade, double entryPrice) {
		super(trade);
		entryLine = new ReferenceLine(entryPrice);
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
		if(!RateUtils.isEqualOrBetter(price, entryLine, super.trade.getIsLong(), true)){
			super.execute(price);
		}
	}

	@Override
	public void accept(OhlcPrice price) {
		// TODO Auto-generated method stub
		
	}
	
	

}
