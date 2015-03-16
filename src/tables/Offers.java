package tables;

import com.fxcore2.O2GOfferTableRow;
import com.fxcore2.O2GOffersTable;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;

public class Offers extends FXTable<O2GOffersTable, O2GOfferTableRow>{
	
	public Offers(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.OFFERS);
	}
	

}

