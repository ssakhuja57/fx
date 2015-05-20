package com.peebeekay.fx.simulation.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.peebeekay.fx.utils.Logger;

public class DbDataSource  extends ADataSource implements Runnable{
	
	private Queue<Price> cache; //for now we can let the cache grow indefinitely since we aren't restricted on memory
	private Boolean stopSignal;
	private volatile boolean tick;
	
	public DbDataSource()
	{
		super();
		cache = new LinkedList<Price>();
		stopSignal = false;
		tick = false;
	}
	
	private void getDataFromDb()
	{
		Random rng = new Random();
		double bid = rng.nextDouble()*3;
		double ask = rng.nextDouble()*3;
		Date nowTime = Calendar.getInstance().getTime();
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
	private synchronized void waitForTick(){
		try{
			wait();
		}catch(InterruptedException e){
			
		}
	}
	public synchronized void stop(){
		stopSignal = true;
		notifyAll();
	}
	@Override
	public void run() {
		getDataFromDb();
		while(!stopSignal){
			waitForTick();
			if(tick){
				publish();
				tick = false;
			}
		}
	}
		
	

	@Override
	public ArrayList<Price> getHistorical(Calendar start, Calendar end, DataType type) {
		//query the db for the prices
		ArrayList<Price> prices = new ArrayList<Price>();
		Random rng = new Random();
		for(int i =0; i<14; i++){
			prices.add(new Price(rng.nextDouble()*3,rng.nextDouble()*3, Calendar.getInstance().getTime()));
		}
		return prices;
	}

	@Override
	public synchronized void onTick() {
		tick = true;
		notifyAll();
	}

	@Override
	public void onThirty() {
		//Nothing??
		
	}

	@Override
	public boolean stillHasData() {
		// TODO Auto-generated method stub
		return false;
	}

}
