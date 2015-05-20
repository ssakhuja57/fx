package com.peebeekay.fx.simulation;

import java.util.ArrayList;

import com.peebeekay.fx.simulation.data.distributors.ADataDistributor;

public class SimulationClock {
	
	private ArrayList<ADataDistributor> dataSources;
	public SimulationClock(){
		dataSources = new ArrayList<ADataDistributor>();
	}
	
	public void registerDatasource(ADataDistributor ds){
		dataSources.add(ds);
	}
	public void run(){
		boolean stillRun = true;
		while(stillRun){
			for(ADataDistributor ds: dataSources){
				stillRun |= ds.stillHasData();
				ds.onTick();
			}
		}
		
	}

}
