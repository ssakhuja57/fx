package com.peebeekay.fx.simulation.trader;

import java.util.HashMap;

import com.peebeekay.fx.simulation.monitor.ICloseMonitor;
import com.peebeekay.fx.simulation.monitor.IOpenMonitor;

public class TradeManager {
	
	private HashMap<Trade, IOpenMonitor> openMonitors;
	private HashMap<Trade, ICloseMonitor> closeMonitors;

}
