package com.peebeekay.fx.tables;

import java.util.ArrayList;
import java.util.List;

import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GOrdersTable;
import com.fxcore2.O2GTableIterator;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;
import com.peebeekay.fx.brokers.fxcm.FxcmUtils;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.trades.Order;

public class Orders extends FXTable<O2GOrdersTable, O2GOrderTableRow>{
	
	public Orders(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.ORDERS);
	}
	
	public List<Order> getAllOrders(){
		List<Order> res = new ArrayList<Order>();
		for(int i=0; i<table.size(); i++)
			res.add(FxcmUtils.getOrder(table.getRow(i)));
		return res;
	}
	
	public ArrayList<String[]> getAllOCOOrderIDs(){
		ArrayList<String[]> res = new ArrayList<String[]>();
		for (int i=0;i<table.size();i++){
			O2GOrderTableRow row = table.getRow(i);
			if(row.getContingencyType() == 1){ //OCO orders only
				res.add(new String[]{row.getAccountID(), row.getOrderID()});
			}
		}
		return res;
	}
	
	public String[] getOCOOrderIDs(Pair pair, String buySell){
		O2GTableIterator iterator = new O2GTableIterator();
        String[] columnNames = new String[] { "OfferID", "BuySell", "ContingencyType" }; //OCO orders only
        Object[] columnValues = new Object[] { String.valueOf(pair.id), buySell, 1 };
        O2GOrderTableRow row =(O2GOrderTableRow) table.getNextRowByMultiColumnValues(columnNames, columnValues, iterator);
        return new String[]{row.getAccountID(), row.getOrderID()};
	}
	
	

}
