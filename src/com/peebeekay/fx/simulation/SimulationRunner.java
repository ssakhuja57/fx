package com.peebeekay.fx.simulation;

import com.peebeekay.fx.simulation.data.distributors.ADataDistributor;
import com.peebeekay.fx.simulation.data.distributors.TickDataDistributor;
import com.peebeekay.fx.simulation.trader.ATrader;
import com.peebeekay.fx.simulation.trader.SimpleRSITrader;

public class SimulationRunner {
	
	
	
	
	public static void main(String[] argv){
		
		ADataDistributor ds = new TickDataDistributor();
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
