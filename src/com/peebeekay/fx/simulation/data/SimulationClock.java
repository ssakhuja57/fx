package com.peebeekay.fx.simulation.data;

import java.util.ArrayList;

public class SimulationClock {
	
	private ArrayList<ADataSource> dataSources;
	public SimulationClock(){
		dataSources = new ArrayList<ADataSource>();
	}
	
	public void registerDatasource(ADataSource ds){
		dataSources.add(ds);
	}
	public void run(){
		boolean stillRun = true;
		while(stillRun){
			for(ADataSource ds: dataSources){
				stillRun |= ds.stillHasData();
				ds.onTick();
			}
		}
		
	}

}
