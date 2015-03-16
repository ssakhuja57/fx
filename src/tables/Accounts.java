package tables;

import com.fxcore2.O2GAccountTableRow;
import com.fxcore2.O2GAccountsTable;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;

public class Accounts extends FXTable<O2GAccountsTable, O2GAccountTableRow>{
	
	public Accounts(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.ACCOUNTS);
	}
}
