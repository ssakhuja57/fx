package com.peebeekay.fx.trades;

import java.util.Date;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.monitors.cancel.ACancelTradeMonitor;
import com.peebeekay.fx.simulation.monitors.close.ACloseTradeMonitor;
import com.peebeekay.fx.simulation.monitors.open.AOpenTradeMonitor;
import com.peebeekay.fx.utils.StringUtils;

public class Trade {
	
	private String id;
	private String accountId;
	private Pair pair;
	private boolean isLong;
	private Date createdTime;
	
	// can be modified by trade table listener
	private int lots;
	private double stopPrice;
	
	private double openPrice;
	private Date openTime;
	private AOpenTradeMonitor openMethod;
	private double initialStopPrice;
	
	private double closePrice;
	private Date closeTime;
	private ACloseTradeMonitor closeMethod;
	
	private Date cancelTime;
	private ACancelTradeMonitor cancelMethod;

	private String notes;
	
	public Trade(String id, String accountId, Pair pair, boolean isLong, int lots, double stopPrice){
		this.id = id;
		this.accountId = accountId;
		this.pair = pair;
		this.isLong = isLong;
		this.lots = lots;
		this.stopPrice = stopPrice;
	}
	
	
	public void setNotes(String notes){
		this.notes = notes;
	}
	
	public String getId(){
		return id;
	}
	
	public String getAccountId(){
		return accountId;
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
	
	public double getStopPrice(){
		return stopPrice;
	}

	public double getOpenPrice() {
		return openPrice;
	}
	
	public Date getOpenTime() {
		return openTime;
	}
	
	public double getInitialStopPrice(){
		return initialStopPrice;
	}

	public AOpenTradeMonitor getOpenMethod() {
		return openMethod;
	}

	public double getClosePrice() {
		return closePrice;
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
	
	public void setOpenPrice(double price){
		this.openPrice = price;
	}
	
	public void setOpenTime(Date time){
		this.openTime = time;
	}
	
	public void setInitialStopPrice(double price){
		this.initialStopPrice = price;
	}
	
	// constantly updated by listeners
	
	public void updateStopPrice(double stopPrice){
		this.stopPrice = stopPrice;
	}
	
	public void updateLots(int lots){
		this.lots = lots;
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
