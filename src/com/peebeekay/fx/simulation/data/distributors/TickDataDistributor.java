package com.peebeekay.fx.simulation.data.distributors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.data.types.Tick;

public class TickDataDistributor extends ADataDistributor implements Runnable{
	
	private Queue<Tick> cache; //for now we can let the cache grow indefinitely since we aren't restricted on memory
	private Boolean stopSignal;
	private volatile boolean fire;
	private volatile Calendar currTime;
	
	public TickDataDistributor(IDataSource source, Pair pair, Calendar start, Calendar end)
	{
		super(source, pair, start, end);
		this.pair = pair;
		cache = new LinkedList<Tick>();
		stopSignal = false;
		fire = false;
	}
	
	
	
	@Override
	public void updateCache(){
		Calendar now = Calendar.getInstance();
		cache.addAll(super.source.getTicks(pair, now, now));
	}
	
	
	private void publish(){
		Boolean allReady = true;
		for(IDataSubscriber subscriber: subscribers){
			allReady &= subscriber.isReady();		
		}
		if(allReady){
			for(IDataSubscriber subscriber: subscribers){
				Tick publishPrice = cache.poll();
				while(publishPrice.getTime().after(currTime)){
					subscriber.accept(publishPrice);
					publishPrice = cache.poll();
				}				
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
		updateCache();
		while(!stopSignal){
			waitForTick();
			if(tick){
				publish();
				tick = false;
			}
		}
	}
		
	

	@Override
	public ArrayList<Tick> getHistorical(Calendar start, Calendar end, Interval type) {
		//query the db for the prices
		ArrayList<Tick> prices = new ArrayList<Tick>();
		
	}

	@Override
	public synchronized void onFire(Calendar currTime) {
		fire = true;
		this.currTime = currTime;
		notifyAll();
	}


	@Override
	public boolean stillHasData(){
		return false;
	}

}
