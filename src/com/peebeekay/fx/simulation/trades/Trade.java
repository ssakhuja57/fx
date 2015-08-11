package com.peebeekay.fx.simulation.trades;

import java.util.Date;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.monitors.cancel.ACancelTradeMonitor;
import com.peebeekay.fx.simulation.monitors.close.ACloseTradeMonitor;
import com.peebeekay.fx.simulation.monitors.open.AOpenTradeMonitor;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.StringUtils;

public class Trade {
	
	private int id;
	private Pair pair;
	private Status status;
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
	
	public Trade(int id, Pair pair, boolean isLong, int lots){
		this.id = id;
		this.pair = pair;
		status = Status.WAITING;
		this.isLong = isLong;
		this.lots = lots;
		createdTime = new Date();
	}
	
	public enum Status{
		WAITING(0), CANCELLED(1), OPEN(1), CLOSED(2);
		public int value;
		private Status(int value){
			this.value = value;
		}
	}
	
	public boolean open(Tick price, AOpenTradeMonitor method){
		if(!checkTrans(status, Status.OPEN)) 
			return false;
		Logger.info("opening trade " + id);
		status = Status.OPEN;
		this.openTick = price;
		if(isLong)
			openPrice = price.getAsk();
		else
			openPrice = price.getBid();
		openTime = price.getTime();
		openMethod = method;
		return true;
	}
	
	public boolean close(Tick price, ACloseTradeMonitor method){
		if(!checkTrans(status, Status.CLOSED))
			return false;
		Logger.info("closing trade " + id);
		status = Status.CLOSED;
		this.closeTick = price;
		if(isLong)
			closePrice = price.getBid();
		else
			closePrice = price.getAsk();
		closeTime = price.getTime();
		closeMethod = method;
		return true;
	}
	
	
	public boolean cancel(ACancelTradeMonitor method){
		if(!checkTrans(status, Status.CANCELLED))
			return false;
		status = Status.CANCELLED;
		cancelMethod = method;
		return true;
	}
	
	
	
	private boolean checkTrans(Status s1, Status s2){
		if(s1 == Status.WAITING && s2 == Status.OPEN)
			return true;
		if(s1 == Status.OPEN && s2 == Status.CLOSED)
			return true;
		if(s1 == Status.WAITING && s2 == Status.CANCELLED)
			return true;
		return false;
	}
	
	public void setNotes(String notes){
		this.notes = notes;
	}
	
	public int getId(){
		return id;
	}
	
	public Pair getPair(){
		return pair;
	}
	
	public Date getCreatedTime(){
		return createdTime;
	}
	
	public Status getStatus(){
		return status;
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

	public ACloseTradeMonitor getCloseMethod() {
		return closeMethod;
	}
	
	public ACancelTradeMonitor getCancelMethod(){
		return cancelMethod;
	}
	
	public Date getCancelTime(){
		return cancelTime;
	}
	
	public String getNotes(){
		return notes;
	}
	
	@Override
	public int hashCode(){
		return id;
	}
	
	public static String getHeaderRow(){
		return "id,pair,lots,status,createdTime,isLong," +
				"openTime,openPrice,openMethod," +
				"closeTime,closePrice,closeMethod," +
				"cancelTime,cancelMethod," +
				"notes";
	}
	
	public String getSummary(){
		Object[] fields = new Object[] {String.valueOf(id),pair,lots,status,
				createdTime,isLong,
				openTime,openPrice,openMethod,
				closeTime,closePrice,closeMethod,
				cancelTime,cancelMethod,
				notes};
		return StringUtils.arrayToString(fields, ",");
	}

}
