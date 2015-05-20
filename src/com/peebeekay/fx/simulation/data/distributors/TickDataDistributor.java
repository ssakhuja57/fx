package com.peebeekay.fx.simulation.data.distributors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.data.types.Tick;

public class TickDataDistributor  extends ADataDistributor implements Runnable{
	
	Pair pair;
	private Queue<Tick> cache; //for now we can let the cache grow indefinitely since we aren't restricted on memory
	private Boolean stopSignal;
	private volatile boolean tick;
	
	public TickDataDistributor(IDataSource source, Pair pair)
	{
		super(source);
		this.pair = pair;
		cache = new LinkedList<Tick>();
		stopSignal = false;
		tick = false;
	}
	
//	private void getDataFromDb()
//	{
//		Random rng = new Random();
//		double bid = rng.nextDouble()*3;
//		double ask = rng.nextDouble()*3;
//		Date nowTime = Calendar.getInstance().getTime();
//		cache.add(new Tick(bid, ask, nowTime));
//	}
	
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
			Tick publishPrice = cache.poll();
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
	public ArrayList<Tick> getHistorical(Calendar start, Calendar end, DataType type) {
		//query the db for the prices
		ArrayList<Tick> prices = new ArrayList<Tick>();
		Random rng = new Random();
		for(int i =0; i<14; i++){
			prices.add(new Tick(pair, rng.nextDouble()*3,rng.nextDouble()*3, Calendar.getInstance().getTime()));
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
