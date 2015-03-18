package session;

import info.Pairs;
import listeners.ResponseListener;
import listeners.SessionStatusListener;
import rates.RateTools;
import tables.Accounts;
import tables.ClosedTrades;
import tables.Offers;
import tables.Orders;
import tables.Summaries;
import tables.Trades;
import actions.PositionActions;

import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GSession;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableManagerMode;
import com.fxcore2.O2GTradeTableRow;
import com.fxcore2.O2GTransport;

import db.DBManager;

public class SessionManager {
	
	public O2GSession session;
	public O2GTableManager tableMgr;
	public SessionStatusListener statusListener;
	public ResponseListener responseListener;
	public DBManager dbMgr;
	
	//tables
	public Accounts accountsTable;
	public ClosedTrades closedTradesTable;
	public Offers offersTable;
	public Orders ordersTable;
	public Summaries summariesTable;
	public Trades tradesTable;
	
	String[] accounts;
	
	
	
	public SessionManager(String login, String password, String DemoOrReal, String account1, String account2){
		
		session = O2GTransport.createSession();
		
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
        //dbMgr.close(); //not needed for now
	}
	
	public String createMarketOrder(String pair, String buySell, int amount) throws InterruptedException{
		String accountID = getAccountID(pair);
		String requestID = PositionActions.createMarketOrder(this, accountID, pair, buySell, amount, responseListener);
		return requestID;
		
	}
	
	public String createEntryOrderWithStop(String pair, String buySell, int amount, double rate, 
			int stopOffset, boolean trailStop) throws InterruptedException{
		String accountID = getAccountID(pair);
		String requestID = PositionActions.createEntryOrderWithStop(this, accountID, pair, buySell, amount, rate, 
				stopOffset, trailStop, responseListener);
		return requestID;
	}
	
	public String createOpposingOCOEntryOrdersWithStops(String pair, int amount, 
			int pipBuffer, int stopOffset, boolean trailStop){
		String accountID = getAccountID(pair);
		double longRate = RateTools.addPips(offersTable.getBuyRate(pair), pipBuffer);
		double shortRate = RateTools.addPips(offersTable.getSellRate(pair), -pipBuffer);
		String requestID = PositionActions.createOpposingOCOEntryOrdersWithStops(this, accountID, 
				pair, amount, longRate, shortRate, stopOffset, trailStop, responseListener);
		return requestID;
	}
	
	public String closeTrade(String pair, String buySell) throws InterruptedException{
		O2GTradeTableRow trade = tradesTable.getTradeRow(pair, buySell);
		if (trade == null){
			System.out.println("No open positions found for " + pair + ":" + buySell);
			return null;
		}
		String requestID = PositionActions.closeTrade(this, trade.getAccountID(), trade.getTradeID(), 
				pair, buySell, trade.getAmount(), responseListener);
		return requestID;
	}
	
	public String cancelOrder(String pair, String buySell) throws InterruptedException{
		O2GOrderTableRow order = ordersTable.getTradeRow(pair, buySell);
		String requestID = PositionActions.cancelOrder(this, order.getAccountID(), order.getOrderID(), responseListener);
		return requestID;
	}
	
	private String getAccountID(String pair){
		return accounts[Pairs.getAccount(pair) - 1];
	}
	
	
	
	
	
	
}
