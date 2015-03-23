package tables;

import com.fxcore2.O2GAccountRow;
import com.fxcore2.O2GAccountTableRow;
import com.fxcore2.O2GAccountsTable;
import com.fxcore2.O2GTableIterator;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;

public class Accounts extends FXTable<O2GAccountsTable, O2GAccountTableRow>{
	
	public Accounts(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.ACCOUNTS);
	}
	
	public O2GAccountRow getAccountRow(String accountID){
		return (O2GAccountRow)table.getNextGenericRowByColumnValue("AccountID", accountID, new O2GTableIterator());
	}
	
	public double getBalance(String accountID){
		return getAccountRow(accountID).getBalance();
	}
}
