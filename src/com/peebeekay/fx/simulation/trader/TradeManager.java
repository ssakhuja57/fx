package com.peebeekay.fx.simulation.trader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.monitors.ATradeMonitor;
import com.peebeekay.fx.simulation.monitors.cancel.ACancelTradeMonitor;
import com.peebeekay.fx.simulation.monitors.close.ACloseTradeMonitor;
import com.peebeekay.fx.simulation.monitors.open.AOpenTradeMonitor;
import com.peebeekay.fx.simulation.trades.Trade;
import com.peebeekay.fx.simulation.trades.Trade.Status;
import com.peebeekay.fx.utils.Logger;

public class TradeManager implements IDataSubscriber{
	
	private String resultFolder;
	private String traderName;
	private Map<Trade,MonitorSet> monitors = new HashMap<Trade,MonitorSet>();
	private int maxConcurrentTrades;
	private int numTrades = 0;
	
	public TradeManager(String resultOutputFolder, String traderName, int maxConcurrentTrades){
		this.resultFolder = resultOutputFolder;
		this.traderName = traderName;
		this.maxConcurrentTrades = maxConcurrentTrades;
	}
	
	public Trade createTrade(Pair pair, Boolean isLong, int lots) throws TradeCreationException{
		Logger.info("trader " + traderName + " creating trade for " + pair);
		if(getOpenTradeCount() >= maxConcurrentTrades && maxConcurrentTrades != 0){
			Logger.debug("already at max concurrent trades of " + maxConcurrentTrades);
			throw new TradeCreationException();
		}
		Trade newTrade = new Trade(numTrades, pair, isLong, lots);
		monitors.put(newTrade, new MonitorSet());
		numTrades++;
		return newTrade;
	}
	
	public int getOpenTradeCount(){
		int count = 0;
		for(Trade t: monitors.keySet()){
			if(t.getStatus() == Status.OPEN)
				count++;
		}
		return count;
	}
	
	public void logResults() throws IOException{
		File f = new File(resultFolder + "\\tradelog-" + traderName);
		FileWriter fw = new FileWriter(f);
		fw.write(Trade.getHeaderRow() + "\n");
		fw.flush();
		for(Trade t: monitors.keySet()){
			fw.write(t.getSummary() + "\n");
			fw.flush();
		}
		fw.close();
	}
	
	@Override
	public void accept(Tick price){
		for(Trade t: monitors.keySet()){
			MonitorSet set = monitors.get(t);
			for(ATradeMonitor m1: set.openMonitors)
				m1.accept(price);
			for(ATradeMonitor m2: set.cancelTradeMonitors)
				m2.accept(price);
			for(ATradeMonitor m3: set.closeMonitors)
				m3.accept(price);
		}
	}
	
	public void updateTrade(Trade trade, AOpenTradeMonitor monitor){
		monitors.get(trade).openMonitors.add(monitor);
			
	}
	
	public void updateTrade(Trade trade, ACloseTradeMonitor monitor){
		monitors.get(trade).closeMonitors.add(monitor);
	}
	
	public void updateTrade(Trade trade, ACancelTradeMonitor monitor){
		monitors.get(trade).cancelTradeMonitors.add(monitor);
	}
	
	public void addTradeNotes(Trade trade, String notes){
		trade.setNotes(notes);
	}
	
	
	
	class MonitorSet{
		public Queue<AOpenTradeMonitor> openMonitors;
		public Queue<ACloseTradeMonitor> closeMonitors;
		public Queue<ACancelTradeMonitor> cancelTradeMonitors;
		public MonitorSet()
		{
			openMonitors = new LinkedList<AOpenTradeMonitor>();
			closeMonitors = new LinkedList<ACloseTradeMonitor>();
			cancelTradeMonitors = new LinkedList<ACancelTradeMonitor>();
			
		}
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(OhlcPrice price) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean isReady() {
		// TODO Auto-generated method stub
		return null;
	}	


}
