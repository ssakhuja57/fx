package info;

import com.fxcore2.IO2GEachRowListener;
import com.fxcore2.O2GRow;
import com.fxcore2.O2GTable;
import com.fxcore2.O2GTableColumn;
import com.fxcore2.O2GTableColumnCollection;

//package info;
//
//import com.fxcore2.IO2GEachRowListener;
//import com.fxcore2.O2GAccountsTable;
//import com.fxcore2.O2GRow;
//import com.fxcore2.O2GTable;
//import com.fxcore2.O2GTableColumn;
//import com.fxcore2.O2GTableColumnCollection;
//import com.fxcore2.O2GTableType;
//import com.fxcore2.O2GTradesTable;
//
//public class Temp {
//	public class EachRowListener implements IO2GEachRowListener {
//
//		@Override
//		public void onEachRow(String arg0, O2GRow arg1) {
//			// TODO Auto-generated method stub
//			
//		} 
//	
//	// Get tables, create listener, call printTable method
//    if (tableManager.getStatus() == O2GTableManagerStatus.TABLES_LOADED) {
//        O2GAccountsTable accountsTable = (O2GAccountsTable)tableManager.getTable(O2GTableType.ACCOUNTS);
//        O2GTradesTable tradesTable = (O2GTradesTable)tableManager.getTable(O2GTableType.TRADES);
//        EachRowListener eachRowListener = new EachRowListener();
//        printTable(accountsTable,eachRowListener);
//        printTable(tradesTable,eachRowListener);
//    }
// 
//    // Print table using IO2GEachRowListener
//    public static void printTable(O2GTable table, IO2GEachRowListener listener) {
//        if (table.size() == 0)
//            System.out.println("Table " + table.getType() + " is empty!");
//        else {
//            table.forEachRow(listener);
//        }
//    }
//    // Implementation if IO2GEachRowListener interface public method onEachRow
//    public void onEachRow(String rowID, O2GRow rowData) {
//        System.out.println("\nPrinting a row from the " + rowData.getTableType() + " table.\n");
//        O2GTableColumnCollection collection = rowData.getColumns();
//        for (int i = 0; i < collection.size(); i++) {
//           O2GTableColumn column = collection.get(i);
//           System.out.print(column.getId() + "=" + rowData.getCell(i) + ";");
//        }
//        System.out.println();
//    }
//}




//
//	
//	static class EachRowListener implements IO2GEachRowListener {
//
//		@Override
//	    public void onEachRow(String rowID, O2GRow rowData) {
//	        //System.out.println("\nPrinting a row from the " + rowData.getTableType() + " table.\n");
//	        O2GTableColumnCollection collection = rowData.getColumns();
//	        for (int i = 0; i < collection.size(); i++) {
//	           O2GTableColumn column = collection.get(i);
//	           System.out.print(column.getId() + "=" + rowData.getCell(i) + ";");
//	        }
//	        System.out.println();
//		}
//	}
//
// 
//    // Print table using IO2GEachRowListener
//    public static void printTable(O2GTable table, IO2GEachRowListener listener) {
//        if (table.size() == 0)
//            System.out.println("Table " + table.getType() + " is empty!");
//        else {
//            table.forEachRow(listener);
//        }
//    }
//
//}
