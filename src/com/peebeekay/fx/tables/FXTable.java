package com.peebeekay.fx.tables;

import com.fxcore2.IO2GEachRowListener;
import com.fxcore2.O2GRow;
import com.fxcore2.O2GTable;
import com.fxcore2.O2GTableColumn;
import com.fxcore2.O2GTableColumnCollection;
import com.fxcore2.O2GTableIterator;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTradeTableRow;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.utils.Logger;

public abstract class FXTable<TableClass extends O2GTable, TableRowClass extends O2GRow> {
	
	protected TableClass table;

	@SuppressWarnings("unchecked")
	public FXTable(O2GTableManager tableMgr, O2GTableType tableType){
		table = (TableClass) tableMgr.getTable(tableType);
	}
	
	@SuppressWarnings("unchecked")
	public TableRowClass getTradeRow(Pair pair, String buySell){
		O2GTableIterator iterator = new O2GTableIterator();
        String[] columnNames = new String[] { "OfferID", "BuySell" };
        Object[] columnValues = new Object[] { String.valueOf(pair.id), buySell };
        return (TableRowClass) table.getNextGenericRowByMultiColumnValues(columnNames, columnValues, iterator);
	}
	
	public String getTradeIDs(Pair pair, String buySell){
		O2GTradeTableRow trade = (O2GTradeTableRow) getTradeRow(pair, buySell);
        return trade.getAccountID() + ":" + trade.getTradeID();
        
	}
	
	
    // Print table using IO2GEachRowListener
    public void printTable() {
    	if (table.size() == 0)
            Logger.error("Table " + table.getType() + " is empty!");
        else {
            table.forEachRow(new EachRowListenerPrint());
        }
    }
    
	protected static class EachRowListenerPrint implements IO2GEachRowListener {

		@Override
	    public void onEachRow(String rowID, O2GRow rowData) {
	        //System.out.println("\nPrinting a row from the " + rowData.getTableType() + " table.\n");
	        O2GTableColumnCollection collection = rowData.getColumns();
	        for (int i = 0; i < collection.size(); i++) {
	           O2GTableColumn column = collection.get(i);
	           System.out.println(column.getId() + "=" + rowData.getCell(i) + ";");
	        }
		}
	}
	
}
