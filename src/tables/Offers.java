package tables;

import info.Pairs;

import com.fxcore2.O2GOfferTableRow;
import com.fxcore2.O2GOffersTable;
import com.fxcore2.O2GTableIterator;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;

public class Offers extends FXTable<O2GOffersTable, O2GOfferTableRow>{
	
	public Offers(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.OFFERS);
	}
	
	private O2GOfferTableRow getRateRow(String pair){
		return table.getNextRowByColumnValue("OfferID", Pairs.getID(pair), new O2GTableIterator());
	}
	
	public double getBuyRate(String pair){
		return getRateRow(pair).getBid();
	}
	
	public double getSellRate(String pair){
		return getRateRow(pair).getAsk();
	}
	
	public double getLow(String pair){
		return getRateRow(pair).getLow();
	}
	
	public double getHigh(String pair){
		return getRateRow(pair).getHigh();
	}
	
	
}

