package com.peebeekay.fx.simulation.monitors.cancel;

import java.util.Calendar;

import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.trades.Trade;

public class TimeCancel extends ACancelTradeMonitor{

	Calendar cancelTime;
	
	public TimeCancel(Trade trade, Calendar cancelTime) {
		super(trade);
		this.cancelTime = cancelTime;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(Tick price) {
		if(!super.checkValid())
			return;
		if(!price.getTime().before(cancelTime.getTime())){
			super.execute(price);
		}
	}

	@Override
	public void accept(OhlcPrice price) {
		// TODO Auto-generated method stub
		
	}

}
