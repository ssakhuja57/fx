package com.peebeekay.fx.tables;

import java.util.ArrayList;
import java.util.List;

import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTradeTableRow;
import com.fxcore2.O2GTradesTable;
import com.peebeekay.fx.brokers.fxcm.FxcmUtils;
import com.peebeekay.fx.trades.Trade;

public class Trades extends FXTable<O2GTradesTable, O2GTradeTableRow>{
	
	public Trades(O2GTableManager tableMgr){
		super(tableMgr, O2GTableType.TRADES);
	}
	
	public List<Trade> getAllTrades(){
		List<Trade> res = new ArrayList<Trade>();
		for(int i=0; i<table.size(); i++)
			res.add(FxcmUtils.getTrade(table.getRow(i)));
		return res;
	}
	

	
}
