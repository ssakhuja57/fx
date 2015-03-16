package tables;

import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GOrdersTable;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;

public class Orders extends FXTable<O2GOrdersTable, O2GOrderTableRow>{
	
	public Orders(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.ORDERS);
	}
	

}
