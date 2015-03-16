package session;

import java.util.HashMap;

import listeners.ResponseListener;
import listeners.SessionStatusListener;
import tables.Accounts;
import tables.ClosedTrades;
import tables.Offers;
import tables.Orders;
import tables.Summaries;
import tables.Trades;
import actions.PositionActions;

import com.fxcore2.Constants;
import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GResponse;
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
	public Accounts accounts;
	public ClosedTrades closedTrades;
	public Offers offers;
	public Orders orders;
	public Summaries summaries;
	public Trades trades;
	
	String longAccount;
	String shortAccount;
	
	
	
	public SessionManager(String login, String password, String DemoOrReal, String longAccount, String shortAccount){
		
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
        
        this.longAccount = longAccount;
        this.shortAccount = shortAccount;
        
        tableMgr = session.getTableManager();
        dbMgr = new DBManager();
        
        //initialize tables
        accounts = new Accounts(tableMgr);
        closedTrades = new ClosedTrades(tableMgr);
        offers = new Offers(tableMgr);
        orders = new Orders(tableMgr);
        summaries = new Summaries(tableMgr);
        trades = new Trades(tableMgr);
	}
	
	
	
	public void close(){
        session.logout();
        session.unsubscribeResponse(responseListener);
        session.unsubscribeSessionStatus(statusListener);
        session.dispose();
        dbMgr.close();
	}
	
	public String createMarketOrder(String pair, String buySell, int amount) throws InterruptedException{
		String accountID = getAccount(buySell);
		String requestID = PositionActions.createMarketOrder(this, accountID, pair, buySell, amount, responseListener);
		O2GResponse resp = responseListener.getResponse(requestID);
		return requestID;
		
	}
	
	public String createEntryStopOrder(String pair, String buySell, int amount, double rate, 
			double stopOffset, boolean trailStop) throws InterruptedException{
		String accountID = getAccount(buySell);
		String requestID = PositionActions.createEntryOrderWithStop(this, accountID, pair, buySell, amount, rate, 
				stopOffset, trailStop, responseListener);
		responseListener.getResponse(requestID);
		return requestID;
	}
	
	public String closeTrade(String pair, String buySell) throws InterruptedException{
		O2GTradeTableRow trade = trades.getTradeRow(pair, buySell);
		if (trade == null){
			System.out.println("No open positions found for " + pair + ":" + buySell);
			return null;
		}
		String requestID = PositionActions.closeTrade(this, trade.getAccountID(), trade.getTradeID(), 
				pair, buySell, trade.getAmount(), responseListener);
		O2GResponse resp = responseListener.getResponse(requestID);
		return requestID;
	}
	
	public String cancelOrder(String pair, String buySell) throws InterruptedException{
		O2GOrderTableRow order = orders.getTradeRow(pair, buySell);
		String requestID = PositionActions.cancelOrder(this, order.getAccountID(), order.getOrderID(), responseListener);
		return requestID;
	}
	
	private String getAccount(String buySell){
		return buySell.equals(Constants.Buy) ? longAccount : shortAccount;
	}
	
	
	
	
	
}
