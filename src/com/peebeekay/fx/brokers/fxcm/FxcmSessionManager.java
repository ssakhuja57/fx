package com.peebeekay.fx.brokers.fxcm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import com.fxcore2.Constants;
import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GSession;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableManagerMode;
import com.fxcore2.O2GTradeTableRow;
import com.fxcore2.O2GTransport;
import com.peebeekay.fx.data.DataProvider;
import com.peebeekay.fx.db.DBManager;
import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.listeners.ResponseListener;
import com.peebeekay.fx.listeners.SessionStatusListener;
import com.peebeekay.fx.session.Credentials;
import com.peebeekay.fx.session.SessionDependent;
import com.peebeekay.fx.session.SessionHolder;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.tables.Accounts;
import com.peebeekay.fx.tables.ClosedTrades;
import com.peebeekay.fx.tables.Offers;
import com.peebeekay.fx.tables.Orders;
import com.peebeekay.fx.tables.Summaries;
import com.peebeekay.fx.tables.Trades;
import com.peebeekay.fx.trades.TradeActionProvider;
import com.peebeekay.fx.trades.specs.CreateTradeSpec;
import com.peebeekay.fx.trades.specs.UpdateTradeSpec;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.RateUtils;

public class FxcmSessionManager implements SessionHolder, TradeActionProvider, DataProvider{
	
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
	
	
	public FxcmSessionManager(Credentials creds, Properties preferences){
		
		this.preferences = preferences;
		session = O2GTransport.createSession();
		dependents = new ArrayList<SessionDependent>();
		
        statusListener = new SessionStatusListener();
        responseListener = new ResponseListener();
        
        session.subscribeSessionStatus(statusListener);
        session.subscribeResponse(responseListener);
        
        session.useTableManager(O2GTableManagerMode.YES, null);
        session.login(creds.getLogin(), creds.getPassword(), "http://www.fxcorporate.com/Hosts.jsp", creds.getDemoOrReal());
        if (!statusListener.waitForLogin()){
        	throw new RuntimeException();
        }
        
        accounts = creds.getAccountNumbers();
        
        
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
	
	
	@Override
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
	
	public String createMarketOrder(Pair pair, String buySell, int amount) throws InterruptedException{
		String accountID = getAccountID(1);
		String requestID = FxcmOrderActions.createMarketOrder(this, accountID, pair, buySell, amount, responseListener);
		return requestID;
		
	}
	
	public String createEntryOrderWithStop(Pair pair, String buySell, int amount, double rate, 
			int stopOffset, boolean trailStop) throws InterruptedException{
		String accountID = getAccountID(1);
		String requestID = FxcmOrderActions.createEntryOrderWithStop(this, accountID, pair, buySell, amount, rate, 
				stopOffset, trailStop, responseListener);
		return requestID;
	}
	
	public String createOpposingOCOEntryOrdersWithStops(Pair pair, int amount, 
			int pipBuffer, int stopOffset, boolean trailStop){
		String accountID = getAccountID(1);
		double longRate = RateUtils.addPips(offersTable.getBuyRate(pair), pipBuffer);
		double shortRate = RateUtils.addPips(offersTable.getSellRate(pair), -pipBuffer);
		String requestID = FxcmOrderActions.createOpposingOCOEntryOrdersWithStops(this, accountID, 
				pair, amount, longRate, shortRate, stopOffset, trailStop, responseListener);
		return requestID;
	}
	
	public void adjustOpposingOCOEntryOrders(Pair pair, int pipBuffer){
		String accountID = ordersTable.getOCOOrderIDs(pair, Constants.Buy)[0];
		String longOrderID = ordersTable.getOCOOrderIDs(pair, Constants.Buy)[1];
		String shortOrderID = ordersTable.getOCOOrderIDs(pair, Constants.Sell)[1];
		double newLongRate = RateUtils.addPips(offersTable.getBuyRate(pair), pipBuffer);
		double newShortRate = RateUtils.addPips(offersTable.getSellRate(pair), -pipBuffer);
		FxcmOrderActions.adjustOpposingOCOEntryOrders(this, accountID, longOrderID, newLongRate, 
				shortOrderID, newShortRate, responseListener);
	}
	
	public String closeTrade(Pair pair, String buySell) throws InterruptedException{
		O2GTradeTableRow trade = tradesTable.getTradeRow(pair, buySell);
		if (trade == null){
			Logger.error("No open positions found for " + pair + ":" + buySell);
			return null;
		}
		String requestID = FxcmOrderActions.closeTrade(this, trade.getAccountID(), trade.getTradeID(), 
				pair, buySell, trade.getAmount(), responseListener);
		return requestID;
	}
	
	public String cancelOrder(Pair pair, String buySell) throws InterruptedException{
		O2GOrderTableRow order = ordersTable.getTradeRow(pair, buySell);
		String requestID = FxcmOrderActions.cancelOrder(this, order.getAccountID(), order.getOrderID(), responseListener);
		return requestID;
	}
	
	public void cancelAllOCOOrders() throws InterruptedException{
		FxcmOrderActions.cancelAllOCOOrders(this, responseListener);
	}
	
	public String setPairSubscription(Pair pair, String status){
		return FxcmOrderActions.setPairSubscription(this, pair, status, responseListener);
	}
	
	public void removeAllPairSubscriptions(){
		FxcmOrderActions.removeAllPairSubscriptions(this, responseListener);
	}

	public void updateMarginsReqs(){
		FxcmOrderActions.updateMarginRequirements(this, responseListener);
	}
	
	public double[] getMarginReqs(Pair pair){
		return FxcmOrderActions.getMarginRequirements(this, pair);
	}
	
	public String getAccountID(int number){
		return this.accounts[number-1];
	}



	@Override
	public Tick getTick(Pair p) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public OhlcPrice getOhlcRow(Pair p, Interval i) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public OhlcPrice getOhlcRow(Pair p, Interval i, Calendar d) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ArrayList<Tick> getTicks(Pair p, Calendar start, Calendar end) {
		return FxcmRateHistory.getTicks(this, p, start, end);
	}



	@Override
	public ArrayList<OhlcPrice> getOhlcRows(Pair p, Interval i, Calendar start,
			Calendar end) {
		return FxcmRateHistory.getOhlcRows(this, p, i, start, end);
	}



	@Override
	public String createOrder(CreateTradeSpec spec) {
		
	}



	@Override
	public void closeTrade(String tradeId) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void updateTrade(String tradeId, UpdateTradeSpec spec) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void cancelOrder(String orderId) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public String getTradeId(String orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
	
	
}
