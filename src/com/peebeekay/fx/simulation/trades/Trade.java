package com.peebeekay.fx.simulation.trades;

import java.util.Date;

import com.peebeekay.fx.simulation.data.Price;
import com.peebeekay.fx.simulation.monitors.close.ACloseTradeMonitor;
import com.peebeekay.fx.simulation.monitors.open.AOpenTradeMonitor;

public class Trade {
	
	private int id;
	private Status status;
	private boolean isLong;
	private int lots;
	private Date createdTime;
	
	private double openPrice;
	private Date openTime;
	private AOpenTradeMonitor openReason;
	
	private double closePrice;
	private Date closeTime;
	private ACloseTradeMonitor closeReason;
	
	public Trade(int id, Date createdTime, boolean isLong, int lots){
		this.id = id;
		status = Status.WAITING;
		this.createdTime = createdTime;
		this.isLong = isLong;
		this.lots = lots;
	}
	
	public enum Status{
		WAITING(0), OPEN(1), CLOSED(2), CANCELLED(-1);
		int value;
		private Status(int value){
			this.value = value;
		}
	}
	
	public void open(Price price, AOpenTradeMonitor reason){
		if(!checkTrans(status, Status.OPEN)) 
			return;
		status = Status.OPEN;
		if(isLong)
			openPrice = price.getAsk();
		else
			openPrice = price.getBid();
		openTime = price.getTime();
		openReason = reason;
	}
	
	public void close(Price price, ACloseTradeMonitor reason){
		if(!checkTrans(status, Status.CLOSED))
			return;
		status = Status.CLOSED;
		if(isLong)
			openPrice = price.getBid();
		else
			openPrice = price.getAsk();
		closeTime = price.getTime();
		closeReason = reason;
	}
	
	public void cancel(){
		if(!checkTrans(status, Status.CANCELLED))
			return;
		status = Status.CANCELLED;
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
	
	public int getId(){
		return id;
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

	public Date getOpenTime() {
		return openTime;
	}

	public AOpenTradeMonitor getOpenReason() {
		return openReason;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public Date getCloseTime() {
		return closeTime;
	}

	public ACloseTradeMonitor getCloseReason() {
		return closeReason;
	}
	
	@Override
	public int hashCode(){
		return id;
	}

	

}
