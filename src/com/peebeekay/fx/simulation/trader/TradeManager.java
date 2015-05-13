package com.peebeekay.fx.simulation.trader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.peebeekay.fx.simulation.monitor.ICloseMonitor;
import com.peebeekay.fx.simulation.monitor.IOpenMonitor;
import com.peebeekay.fx.simulation.monitors.ATradeMonitor;
import com.peebeekay.fx.simulation.monitors.close.ACloseTradeMonitor;
import com.peebeekay.fx.simulation.monitors.open.AOpenTradeMonitor;
import com.peebeekay.fx.simulation.trades.Trade;

public class TradeManager {
	
	private HashMap<Trade,MonitorPair> monitors;
	private int numTrades;
	public TradeManager(){
		numTrades = 0;
	}
	
	public void createTrade(bool isLong, int lots){
		Trade newTrade = new Trade(numTrades, isLong,lots);
		monitors.put(newTrade, new MonitorPair());
		numTrades++;
	}
	
	public void updateTrade(Trade trade,AOpenTradeMonitor monitor){
		if(monitors.containsKey(trade)){
			
		}
		
	}
	
	public void updateTrade(Trade trade, ACloseTradeMonitor monitor){
		
	}
	
	class MonitorPair{
		public Queue<AOpenTradeMonitor> openMonitors;
		public Queue<ACloseTradeMonitor> closeMonitors;
		public MonitorPair()
		{
			openMonitors = new LinkedList<AOpenTradeMonitor>();
			closeMonitors = new LinkedList<ACloseTradeMonitor>();
		}
		
	}
	
	

}
