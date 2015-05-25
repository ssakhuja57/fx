package com.peebeekay.fx.simulation.trades;

import java.lang.reflect.Field;
import java.util.Date;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.simulation.monitors.close.ACloseTradeMonitor;
import com.peebeekay.fx.simulation.monitors.open.AOpenTradeMonitor;
import com.peebeekay.fx.utils.DateUtils;

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
	private AOpenTradeMonitor openReason;
	
	private double closePrice;
	private Tick closeTick;
	private Date closeTime;
	private ACloseTradeMonitor closeReason;
	
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
	
	public void open(Tick price, AOpenTradeMonitor reason){
		if(!checkTrans(status, Status.OPEN)) 
			return;
		status = Status.OPEN;
		this.openTick = price;
		if(isLong)
			openPrice = price.getAsk();
		else
			openPrice = price.getBid();
		openTime = price.getTime();
		openReason = reason;
	}
	
	public void close(Tick price, ACloseTradeMonitor reason){
		if(!checkTrans(status, Status.CLOSED))
			return;
		status = Status.CLOSED;
		this.closeTick = price;
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

	public AOpenTradeMonitor getOpenReason() {
		return openReason;
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

	public ACloseTradeMonitor getCloseReason() {
		return closeReason;
	}
	
	public String getSummary(){
		String res = "";
		for (Field field : this.getClass().getDeclaredFields()) {
		    field.setAccessible(true);
		    String name = field.getName();
		    Object value = null;
			try {
				value = field.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		    res += name + "=" + value + "; ";
		}
		return res;
	}

	
	@Override
	public int hashCode(){
		return id;
	}

	

}
