package tables;

import info.Pairs;

import com.fxcore2.O2GOfferTableRow;
import com.fxcore2.O2GOffersTable;
import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GTableIterator;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;

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
	
	public double getBuyRate(String pair){
		return getRateRow(pair).getAsk();
	}
	
	public double getSellRate(String pair){
		return getRateRow(pair).getBid();
	}
	
	public double getLow(String pair){
		return getRateRow(pair).getLow();
	}
	
	public double getHigh(String pair){
		return getRateRow(pair).getHigh();
	}
	
	public double getPipCost(String pair){
		return getRateRow(pair).getPipCost();
	}
	
	private O2GOfferTableRow getRateRow(String pair){
		return table.getNextRowByColumnValue("OfferID", Pairs.getID(pair), new O2GTableIterator());
	}
	
	
	
	
}

