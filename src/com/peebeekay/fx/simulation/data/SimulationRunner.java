package com.peebeekay.fx.simulation.data;

import com.peebeekay.fx.simulation.trader.ATrader;
import com.peebeekay.fx.simulation.trader.SimpleRSITrader;

public class SimulationRunner {
	
	
	
	
	public static void main(String[] argv){
		
		ADataSource ds = new DbDataSource();
		ATrader td = new SimpleRSITrader(ds);
		SimulationClock simClock = new SimulationClock();
		
		simClock.registerDatasource(ds);
		Thread traderThrd = new Thread(td);
		Thread dataSourceThrd = new Thread(ds);
		traderThrd.start();
		dataSourceThrd.start();
		simClock.run();
		
	}

}
