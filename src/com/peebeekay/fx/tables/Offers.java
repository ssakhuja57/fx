package com.peebeekay.fx.tables;

import com.fxcore2.O2GOfferTableRow;
import com.fxcore2.O2GOffersTable;
import com.fxcore2.O2GTableIterator;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;
import com.peebeekay.fx.info.Pair;

public class Offers extends FXTable<O2GOffersTable, O2GOfferTableRow>{
	
	public Offers(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.OFFERS);
	}
	
	
	public int getSubscriptionCount(){
		int count = 0;
		for (int i=0;i<table.size();i++){
			if(table.getRow(i).getSubscriptionStatus().equals("T")){
				count++;
			}
		}
		return count;
	}
	
	public double getBuyRate(Pair pair){
		return getRateRow(pair).getAsk();
	}
	
	public double getSellRate(Pair pair){
		return getRateRow(pair).getBid();
	}
	
	public double getLow(Pair pair){
		return getRateRow(pair).getLow();
	}
	
	public double getHigh(Pair pair){
		return getRateRow(pair).getHigh();
	}
	
	public double getPipCost(Pair pair){
		return getRateRow(pair).getPipCost();
	}
	
	private O2GOfferTableRow getRateRow(Pair pair){
		return table.getNextRowByColumnValue("OfferID", String.valueOf(pair.id), new O2GTableIterator());
	}
	
	
	
	
}

