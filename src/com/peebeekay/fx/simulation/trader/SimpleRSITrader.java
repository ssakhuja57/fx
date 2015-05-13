package com.peebeekay.fx.simulation.trader;

import java.util.ArrayList;
import java.util.Calendar;

import com.peebeekay.fx.simulation.data.ADataSource;
import com.peebeekay.fx.simulation.data.ADataSource.DataType;
import com.peebeekay.fx.simulation.data.Price;
import com.peebeekay.fx.simulation.indicator.IIndicator;
import com.peebeekay.fx.simulation.indicator.RSI;

public class SimpleRSITrader extends ATrader implements Runnable{
	
	private IIndicator rsi;
	private static final int period = 14;
	private static final int HIGH_MARK = 70;
	private static final double LOW_MARK = 30;
	private double prevRsi;
	private double pointsReceived;
	private volatile Boolean isReady;
	private Boolean stillRunning;
	
	private enum Signal{
		HOLD,BUY,SELL
	}
	
	public SimpleRSITrader(ADataSource ds){
		super(ds);
		Calendar timeNow = Calendar.getInstance();
		Calendar nowMinus14 = Calendar.getInstance();
		nowMinus14.add(Calendar.MINUTE, -14);
		ArrayList<Price> historicalPrices = ds.getHistorical(nowMinus14, timeNow, DataType.M30);
		rsi = new RSI(period, true, true, historicalPrices);
		prevRsi = rsi.getValue();
		pointsReceived = 0;
		isReady = true;
		stillRunning = true;
	}

	public Signal chooseAction() {
		if(prevRsi < LOW_MARK && rsi.getValue() > LOW_MARK )
			return Signal.BUY;
		if(prevRsi > HIGH_MARK && rsi.getValue() < HIGH_MARK)
			return Signal.SELL;
		return Signal.HOLD;
	}
	
	@Override
	public synchronized void accept(Price price) {
		rsi.addDataPoint(price);
		pointsReceived++;
		notifyAll();
	}
	

	@Override
	public Boolean isReady() {
		return isReady;
	}
	
	private synchronized void waitForData()
	{
		while(isReady)
		{
			try{
				wait();
			}catch(InterruptedException e){}
		}		
	}

	@Override
	public void run() {
		while(!stillRunning){			
			waitForData();
			Signal action = chooseAction();
			prevRsi = rsi.getValue();
			//do action or ignore it..
			isReady = true;
		}
		
	}

	
}
