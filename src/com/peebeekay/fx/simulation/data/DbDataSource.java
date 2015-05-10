package com.peebeekay.fx.simulation.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.peebeekay.fx.utils.Logger;

public class DbDataSource  extends ADataSource implements Runnable{
	
	private Queue<Price> cache;
	private Boolean stopSignal;
	public DbDataSource()
	{
		super();
		cache = new LinkedList<Price>();
		stopSignal = false;
	}
	
	private void publish(){
		Boolean allReady = false;
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

}
