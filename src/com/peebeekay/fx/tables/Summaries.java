package com.peebeekay.fx.tables;

import com.fxcore2.O2GSummaryTable;
import com.fxcore2.O2GSummaryTableRow;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;

public class Summaries extends FXTable<O2GSummaryTable, O2GSummaryTableRow>{
	
	public Summaries(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.SUMMARY);
	}
}
