package com.peebeekay.fx.trades;

import java.util.Date;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.monitors.cancel.ACancelTradeMonitor;
import com.peebeekay.fx.simulation.monitors.close.ACloseTradeMonitor;
import com.peebeekay.fx.simulation.monitors.open.AOpenTradeMonitor;
import com.peebeekay.fx.utils.StringUtils;

public class Trade {
	
	private String id;
	private Pair pair;
	private boolean isLong;
	private int lots;
	private Date createdTime;
	
	private double openPrice;
	private Tick openTick;
	private Date openTime;
	private AOpenTradeMonitor openMethod;
	
	private double closePrice;
	private Tick closeTick;
	private Date closeTime;
	private ACloseTradeMonitor closeMethod;
	
	private Date cancelTime;
	private ACancelTradeMonitor cancelMethod;

	private String notes;
	
	public Trade(String id, Pair pair, boolean isLong, int lots){
		this.id = id;
		this.pair = pair;
		this.isLong = isLong;
		this.lots = lots;
	}
	
	
	public void setNotes(String notes){
		this.notes = notes;
	}
	
	public String getId(){
		return id;
	}
	
	public Pair getPair(){
		return pair;
	}
	
	public Date getCreatedTime(){
		return createdTime;
	}
	
	public boolean getIsLong(){
		return isLong;
	}

	public int getLots() {
		return lots;
	}

	public double getOpenPrice() {
		return openPrice;
	}
	
	public Tick getOpenTick(){
		return openTick;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public AOpenTradeMonitor getOpenMethod() {
		return openMethod;
	}

	public double getClosePrice() {
		return closePrice;
	}
	
	public Tick getCloseTick(){
		return closeTick;
	}

	public Date getCloseTime() {
		return closeTime;
	}


	public String getNotes(){
		return notes;
	}
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
	public static String getHeaderRow(){
		return "id,pair,lots,status,createdTime,isLong," +
				"openTime,openPrice,openMethod," +
				"closeTime,closePrice,closeMethod," +
				"cancelTime,cancelMethod," +
				"notes";
	}
	
	public String getSummary(){
		Object[] fields = new Object[] {String.valueOf(id),pair,lots,
				createdTime,isLong,
				openTime,openPrice,openMethod,
				closeTime,closePrice,closeMethod,
				cancelTime,cancelMethod,
				notes};
		return StringUtils.arrayToString(fields, ",");
	}

}
