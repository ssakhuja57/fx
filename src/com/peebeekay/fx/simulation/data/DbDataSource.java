package com.peebeekay.fx.simulation.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.peebeekay.fx.utils.Logger;

public class DbDataSource  extends ADataSource implements Runnable{
	
	private Queue<Price> cache; //for now we can let the cache grow indefinitely since we aren't restricted on memory
	private Boolean stopSignal;
	public DbDataSource()
	{
		super();
		cache = new LinkedList<Price>();
		stopSignal = false;
	}
	
	private void getDataFromDb()
	{
		Random rng = new Random();
		double bid = rng.nextDouble()*3;
		double ask = rng.nextDouble()*3;
		Calendar nowTime = Calendar.getInstance();
		cache.add(new Price(bid, ask, nowTime));
	}
	
	private void publish(){
		Boolean allReady = true;
		for(IDataSubscriber subscriber: subscribers){
			allReady &= subscriber.isReady();		
		}
		if(allReady){
			Price publishPrice = cache.poll();
			for(IDataSubscriber subscriber: subscribers){
				subscriber.accept(publishPrice);
			}
		}
	}

	@Override
	public void run() {
		long lastSendTime = 0;
		
		while(!stopSignal)
		{
			long sleepTime = (long) (publishingRate*1000);
			long loopStart = System.nanoTime();
			//get data from database
			getDataFromDb();
			//determine if it's time to send the data
			double elapsedTime = (double)(System.nanoTime() - lastSendTime)/1000000000.00;
			if(elapsedTime > publishingRate)
			{
				publish();
				lastSendTime = System.nanoTime();
			}
			long workTime = (System.nanoTime() - loopStart)/1000000; //amount of time spend actually doing work
			if(sleepTime > workTime)
			{ //if we can sleep
				try {
					Thread.sleep(sleepTime- workTime);
				} catch (InterruptedException e) {
					Logger.debug("Could not sleep");
				}
			}
		}
	}

	@Override
	ArrayList<Price> getHistorical(Calendar start, Calendar end, DataType type) {
		//query the db for the prices
		return null;
	}

}
