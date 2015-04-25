package com.peebeekay.fx.session;

import java.util.ArrayList;
import java.util.Properties;

import com.fxcore2.Constants;
import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GSession;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableManagerMode;
import com.fxcore2.O2GTradeTableRow;
import com.fxcore2.O2GTransport;
import com.peebeekay.fx.actions.OrderActions;
import com.peebeekay.fx.db.DBManager;
import com.peebeekay.fx.info.Pairs;
import com.peebeekay.fx.listeners.ResponseListener;
import com.peebeekay.fx.listeners.SessionStatusListener;
import com.peebeekay.fx.rates.RateTools;
import com.peebeekay.fx.tables.Accounts;
import com.peebeekay.fx.tables.ClosedTrades;
import com.peebeekay.fx.tables.Offers;
import com.peebeekay.fx.tables.Orders;
import com.peebeekay.fx.tables.Summaries;
import com.peebeekay.fx.tables.Trades;
import com.peebeekay.fx.utils.Logger;

public class SessionManager {
	
	public O2GSession session;
	private Properties preferences;
	public O2GTableManager tableMgr;
	public SessionStatusListener statusListener;
	public ResponseListener responseListener;
	private ArrayList<SessionDependent> dependents;
	public DBManager dbMgr;
	
	//tables
	public Accounts accountsTable;
	public ClosedTrades closedTradesTable;
	public Offers offersTable;
	public Orders ordersTable;
	public Summaries summariesTable;
	public Trades tradesTable;
	
	String[] accounts;
	public final int subscriptionLimit = 20;
	
	
	public SessionManager(Properties preferences, String login, String password, String DemoOrReal, String account1, String account2){
		
		this.preferences = preferences;
		session = O2GTransport.createSession();
		dependents = new ArrayList<SessionDependent>();
		
        statusListener = new SessionStatusListener();
        responseListener = new ResponseListener();
        
        session.subscribeSessionStatus(statusListener);
        session.subscribeResponse(responseListener);
        
        session.useTableManager(O2GTableManagerMode.YES, null);
        session.login(login, password, "http://www.fxcorporate.com/Hosts.jsp", DemoOrReal);
        if (!statusListener.waitForLogin()){
        	System.exit(1);
        }
        
        accounts = new String[]{account1, account2};
        
        
        tableMgr = session.getTableManager();
        //dbMgr = new DBManager(); // disable for now, not needed
        
        //initialize tables
        accountsTable = new Accounts(tableMgr);
        closedTradesTable = new ClosedTrades(tableMgr);
        offersTable = new Offers(tableMgr);
        ordersTable = new Orders(tableMgr);
        summariesTable = new Summaries(tableMgr);
        tradesTable = new Trades(tableMgr);
	}
	
	
	
	public void close(){
        session.logout();
        session.unsubscribeResponse(responseListener);
        session.unsubscribeSessionStatus(statusListener);
        session.dispose();
        for (SessionDependent dep:dependents){
        	dep.end();
        }
        //dbMgr.close(); //not needed for now
	}
	
	public void registerDependent(SessionDependent dep){
		dependents.add(dep);
	}
	
	public String createMarketOrder(String pair, String buySell, int amount) throws InterruptedException{
		String accountID = getAccountID(pair);
		String requestID = OrderActions.createMarketOrder(this, accountID, pair, buySell, amount, responseListener);
		return requestID;
		
	}
	
	public String createEntryOrderWithStop(String pair, String buySell, int amount, double rate, 
			int stopOffset, boolean trailStop) throws InterruptedException{
		String accountID = getAccountID(pair);
		String requestID = OrderActions.createEntryOrderWithStop(this, accountID, pair, buySell, amount, rate, 
				stopOffset, trailStop, responseListener);
		return requestID;
	}
	
	public String createOpposingOCOEntryOrdersWithStops(String pair, int amount, 
			int pipBuffer, int stopOffset, boolean trailStop){
		String accountID = getAccountID(pair);
		double longRate = RateTools.addPips(offersTable.getBuyRate(pair), pipBuffer);
		double shortRate = RateTools.addPips(offersTable.getSellRate(pair), -pipBuffer);
		String requestID = OrderActions.createOpposingOCOEntryOrdersWithStops(this, accountID, 
				pair, amount, longRate, shortRate, stopOffset, trailStop, responseListener);
		return requestID;
	}
	
	public void adjustOpposingOCOEntryOrders(String pair, int pipBuffer){
		String accountID = ordersTable.getOCOOrderIDs(pair, Constants.Buy)[0];
		String longOrderID = ordersTable.getOCOOrderIDs(pair, Constants.Buy)[1];
		String shortOrderID = ordersTable.getOCOOrderIDs(pair, Constants.Sell)[1];
		double newLongRate = RateTools.addPips(offersTable.getBuyRate(pair), pipBuffer);
		double newShortRate = RateTools.addPips(offersTable.getSellRate(pair), -pipBuffer);
		OrderActions.adjustOpposingOCOEntryOrders(this, accountID, longOrderID, newLongRate, 
				shortOrderID, newShortRate, responseListener);
	}
	
	public String closeTrade(String pair, String buySell) throws InterruptedException{
		O2GTradeTableRow trade = tradesTable.getTradeRow(pair, buySell);
		if (trade == null){
			Logger.error("No open positions found for " + pair + ":" + buySell);
			return null;
		}
		String requestID = OrderActions.closeTrade(this, trade.getAccountID(), trade.getTradeID(), 
				pair, buySell, trade.getAmount(), responseListener);
		return requestID;
	}
	
	public String cancelOrder(String pair, String buySell) throws InterruptedException{
		O2GOrderTableRow order = ordersTable.getTradeRow(pair, buySell);
		String requestID = OrderActions.cancelOrder(this, order.getAccountID(), order.getOrderID(), responseListener);
		return requestID;
	}
	
	public void cancelAllOCOOrders() throws InterruptedException{
		OrderActions.cancelAllOCOOrders(this, responseListener);
	}
	
	public String setPairSubscription(String pair, String status){
		return OrderActions.setPairSubscription(this, pair, status, responseListener);
	}
	
	public void removeAllPairSubscriptions(){
		OrderActions.removeAllPairSubscriptions(this, responseListener);
	}

	public void updateMarginsReqs(){
		OrderActions.updateMarginRequirements(this, responseListener);
	}
	
	public double[] getMarginReqs(String pair){
		return OrderActions.getMarginRequirements(this, pair);
	}
	
	public String getAccountID(String pair){
		return accounts[Pairs.getAccount(pair) - 1];
	}
	
	public String getAccountID(int number){
		return this.accounts[number-1];
	}

	
	
	
	
	
	
}
