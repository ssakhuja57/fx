package tables;

import com.fxcore2.O2GClosedTradeTableRow;
import com.fxcore2.O2GClosedTradesTable;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;

public class ClosedTrades extends FXTable<O2GClosedTradesTable, O2GClosedTradeTableRow>{
	
	
	public ClosedTrades(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.CLOSED_TRADES);
	}
	

}
