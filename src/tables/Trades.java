package tables;

import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTradeTableRow;
import com.fxcore2.O2GTradesTable;

public class Trades extends FXTable<O2GTradesTable, O2GTradeTableRow>{
	
	public Trades(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.TRADES);
	}
	

	
}
