package com.peebeekay.fx.simulation.trader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.monitor.ICloseMonitor;
import com.peebeekay.fx.simulation.monitor.IOpenMonitor;
import com.peebeekay.fx.simulation.monitors.ATradeMonitor;
import com.peebeekay.fx.simulation.monitors.cancel.ACancelTradeMonitor;
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
		monitors.get(trade).openMonitors.add(monitor);
			
	}
	
	public void updateTrade(Trade trade, ACloseTradeMonitor monitor){
		monitors.get(trade).closeMonitors.add(monitor);
	}
	
	
	
	class MonitorPair{
		public Queue<AOpenTradeMonitor> openMonitors;
		public Queue<ACloseTradeMonitor> closeMonitors;
		public Queue<ACancelTradeMonitor> cancelTradeMonitors;
		public MonitorPair()
		{
			openMonitors = new LinkedList<AOpenTradeMonitor>();
			closeMonitors = new LinkedList<ACloseTradeMonitor>();
			cancelTradeMonitors = new LinkedList<ACancelTradeMonitor>();
			
		}
	}	


}
