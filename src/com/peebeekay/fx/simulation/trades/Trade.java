package com.peebeekay.fx.simulation.trades;

import java.util.Date;

import com.peebeekay.fx.simulation.data.Price;
import com.peebeekay.fx.simulation.monitors.close.ACloseTradeMonitor;
import com.peebeekay.fx.simulation.monitors.open.AOpenTradeMonitor;

public class Trade {
	
	private Status status;
	private boolean isLong;
	private int lots;
	
	private double openPrice;
	private Date openTime;
	private AOpenTradeMonitor openReason;
	
	private double closePrice;
	private Date closeTime;
	private ACloseTradeMonitor closeReason;
	
	public Trade(boolean isLong, int lots){
		status = Status.WAITING;
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
		checkTrans(status, Status.OPEN);
		status = Status.OPEN;
		if(isLong)
			openPrice = price.getAsk();
		else
			openPrice = price.getBid();
		openTime = price.getTime();
		openReason = reason;
	}
	
	public void close(Price price, ACloseTradeMonitor reason){
		checkTrans(status, Status.CLOSED);
		status = Status.CLOSED;
		if(isLong)
			openPrice = price.getBid();
		else
			openPrice = price.getAsk();
		closeTime = price.getTime();
		closeReason = reason;
	}
	
	public void cancel(){
		checkTrans(status, Status.CANCELLED);
		status = Status.CANCELLED;
	}
	
	
	private void checkTrans(Status s1, Status s2){
		if(s1 == Status.WAITING && s2 == Status.OPEN)
			return;
		if(s1 == Status.OPEN && s2 == Status.CLOSED)
			return;
		if(s1 == Status.WAITING && s2 == Status.CANCELLED)
			return;
		throw new RuntimeException();
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

	

}
