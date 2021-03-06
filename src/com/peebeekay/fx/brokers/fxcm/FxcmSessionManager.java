package com.peebeekay.fx.brokers.fxcm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.fxcore2.Constants;
import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GSession;
import com.fxcore2.O2GTable;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableManagerMode;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTradeTableRow;
import com.fxcore2.O2GTransport;
import com.peebeekay.fx.data.DataNotFoundException;
import com.peebeekay.fx.data.IDataProvider;
import com.peebeekay.fx.db.DBManager;
import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.listeners.ResponseListener;
import com.peebeekay.fx.listeners.SessionStatusListener;
import com.peebeekay.fx.session.Credentials;
import com.peebeekay.fx.session.Credentials.LoginProperties;
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
import com.peebeekay.fx.trades.IAccountInfoProvider;
import com.peebeekay.fx.trades.ITradeActionProvider;
import com.peebeekay.fx.trades.Order;
import com.peebeekay.fx.trades.OrderCreationException;
import com.peebeekay.fx.trades.Trade;
import com.peebeekay.fx.trades.TradeNotFoundException;
import com.peebeekay.fx.trades.specs.CreateTradeSpec;
import com.peebeekay.fx.trades.specs.CreateTradeSpec.CloseTradeType;
import com.peebeekay.fx.trades.specs.CreateTradeSpec.OpenTradeType;
import com.peebeekay.fx.trades.specs.TradeSpec.TradeProperty;
import com.peebeekay.fx.trades.specs.UpdateTradeSpec;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.RateUtils;

public class FxcmSessionManager implements SessionHolder, ITradeActionProvider, IDataProvider, IAccountInfoProvider {
	
	public O2GSession session;
	private Credentials creds;
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
	double percentMaxAccountUse;
	
	
	public FxcmSessionManager(Credentials creds, double percentMaxAccountUse){
		this.creds = creds;
		this.percentMaxAccountUse = percentMaxAccountUse;
		connect();
	}
	
	public void connect(){
		
		session = O2GTransport.createSession();
		dependents = new ArrayList<SessionDependent>();
		
        statusListener = new SessionStatusListener(this);
        responseListener = new ResponseListener();
        
        session.subscribeSessionStatus(statusListener);
        session.subscribeResponse(responseListener);
        
        session.useTableManager(O2GTableManagerMode.YES, null);
        session.login(creds.getLogin(), creds.getPassword(), "http://www.fxcorporate.com/Hosts.jsp", creds.getDemoOrReal());
        statusListener.waitForLogin();
        
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
        
        // re-register dependents in case re-connecting
        for (SessionDependent dep: dependents){
        	registerDependent(dep);
        }
	}
	
	
	@Override
	public void close(){
        session.logout();
        session.unsubscribeResponse(responseListener);
        session.unsubscribeSessionStatus(statusListener);
        session.dispose();
        for (SessionDependent dep: dependents){
        	dep.close();
        }
        //dbMgr.close(); //not needed for now
	}
	
	public void reconnect(){
		close();
		connect();
		for(SessionDependent dep: dependents)
			dep.reconnect();
	}
	
	public String getLoginProperty(LoginProperties prop){
		return creds.getProperty(prop);
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
	
	public String closeTrade(Pair pair, String buySell) throws InterruptedException, TradeNotFoundException{
		O2GTradeTableRow trade = tradesTable.getTradeRow(pair, buySell);
		if (trade == null){
			Logger.error("No open positions found for " + pair + ":" + buySell);
			throw new TradeNotFoundException();
		}
		String requestID = FxcmOrderActions.closeTrade(this, trade.getAccountID(),
				pair, buySell, trade.getAmount(), responseListener);
		return requestID;
	}
	
	
	public String partialClose(Trade trade, int lots){
		String buySell = trade.getIsLong() ? Constants.Buy : Constants.Sell;
		String requestID = FxcmOrderActions.closeTrade(this, trade.getAccountId(), trade.getPair(), buySell, lots, responseListener);
		return requestID;
	}
	
	public String partialClose(Trade trade, double percent) throws TradeNotFoundException{
		int total = trade.getLots();
		int lots = (int)(percent*total);
		String requestID = partialClose(trade, lots);
		Logger.info("Partial close of " + percent*100 + "%");
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
	
	public O2GTable getTable(O2GTableType type){
		return tableMgr.getTable(type);
	}



	@Override
	public Tick getTick(Pair p) {
		return FxcmUtils.offerToTick(offersTable.getRateRow(p));
	}



	@Override
	public OhlcPrice getOhlcRow(Pair p, Interval i) throws DataNotFoundException {
		return getOhlcRow(p, i, DateUtils.getLastIntervalTime(i));
	}



	@Override
	public OhlcPrice getOhlcRow(Pair p, Interval i, Calendar d) throws DataNotFoundException{
		OhlcPrice row = FxcmRateHistory.getOhlcRow(this, p, i, d);
		if(row == null)
			throw new DataNotFoundException("unable to get " + p + " " + i + " data for " + DateUtils.calToString(d));
		return row;
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
	public String createOrder(CreateTradeSpec spec) throws OrderCreationException {
		Pair pair = spec.getPair();
		int amount = spec.getLots()*1000;
		String buySell = spec.getIsLong() ? Constants.Buy : Constants.Sell;
		OpenTradeType openType = spec.getOpenType();
		CloseTradeType closeType = spec.getCloseType();
		Map<TradeProperty, String> props = spec.getTradeProperties();
		
		String orderId;
		
		if(openType == OpenTradeType.MARKET_OPEN && closeType == CloseTradeType.STOP_CLOSE){
			int stopSize = Integer.parseInt(props.get(TradeProperty.STOP_SIZE));
			orderId = FxcmOrderActions.createMarketOrderWithStop(this, getAccountID(1), pair, buySell, 
					amount, stopSize, true, responseListener);
		}
		else{
			throw new OrderCreationException("open trade and close trade types are not supported yet yo: " + openType + "," + closeType);
		}
		
		return orderId;
	}




	@Override
	public List<Pair> getSubscribedPairs() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void closeTrade(Trade trade) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateTrade(Order order, UpdateTradeSpec spec) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void cancelOrder(Order order) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void adjustStop(Order order, int newStopOffset) {
//		Logger.debug("adjusting order for " + order.getPair());
		newStopOffset = order.getIsLong() ? -newStopOffset : newStopOffset;
		FxcmOrderActions.adjustStop(this, accounts[0], order.getId(), newStopOffset, responseListener);
	}
	
	@Override
	public void adjustStop(Order order, double newRate) {
//		Logger.debug("adjusting order for " + order.getPair());
		FxcmOrderActions.adjustStop(this, accounts[0], order.getId(), newRate, responseListener);
	}
	
	@Override
	public double getPercentMaxAccountUse(){
		return percentMaxAccountUse;
	}

	@Override
	public double getTotalUsableAccountBalance() {
		return accountsTable.getBalance(accounts[0])*percentMaxAccountUse;
	}

	@Override
	public double getAvailableUsableAccountBalance() {
		return getTotalUsableAccountBalance() - accountsTable.getAccountRow(accounts[0]).getUsedMargin();
	}
	
	@Override
	public double getAvailableUsablePercentAccountBalance(){
		return getAvailableUsableAccountBalance()/getTotalUsableAccountBalance();
	}

	@Override
	public int getLots(Pair p, double accountValue) {
		return (int)(accountValue/getMarginReqs(p)[0]);
	}

	
	
	
	
	
	
}
