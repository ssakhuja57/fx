package com.peebeekay.fx.simulation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.peebeekay.fx.simulation.data.distributors.ADataDistributor;

public class SimulationClock extends TimerTask{
	
	private int speed; 
	private Timer timer;
	private ArrayList<ADataDistributor> dataDists;
	private Calendar currentTime;
	
	public SimulationClock(int speed, Calendar startTime){
		this.speed = speed;
		dataDists = new ArrayList<ADataDistributor>();
		timer = new Timer();
		currentTime = startTime;
	}
	
	public void registerDatasource(ADataDistributor ds){
		dataDists.add(ds);
	}
	
	public void begin(){
		timer.schedule(this, 0, 1000/speed);
	}
	
	@Override
	public void run(){
		boolean stillRun = false;
		for(ADataDistributor ds: dataDists){
			stillRun |= ds.stillHasData();
			ds.onFire(currentTime);
		}
		if(!stillRun)
			timer.cancel();
		currentTime.add(Calendar.MILLISECOND, 100);
	}

}
