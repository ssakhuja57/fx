package com.peebeekay.fx.brokers.fxcm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fxcore2.IO2GTableListener;
import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GRow;
import com.fxcore2.O2GTableStatus;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTableUpdateType;
import com.fxcore2.O2GTradeTableRow;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.session.SessionDependent;
import com.peebeekay.fx.trades.ITradeInfoProvider;
import com.peebeekay.fx.trades.Order;
import com.peebeekay.fx.trades.Trade;
import com.peebeekay.fx.trades.TradeNotFoundException;
import com.peebeekay.fx.utils.Logger;

public class FxcmTradeInfoProvider implements ITradeInfoProvider, SessionDependent{
	
	FxcmSessionManager fx;
	
	OrdersListener ol;
	TradesListener tl;
	
	Map<String, Order> orders = new ConcurrentHashMap<String, Order>();
	Map<String, Trade> trades = new ConcurrentHashMap<String, Trade>();
	Map<String, Trade> closedTrades = new ConcurrentHashMap<String, Trade>();
	
	public FxcmTradeInfoProvider(FxcmSessionManager fx){
		this.fx = fx;
		fx.registerDependent(this);
		ol = new OrdersListener();
		tl = new TradesListener();
		connect();
	}
	
	public void connect(){
		ol.connect();
		tl.connect();
	}
	
	@Override
	public void close(){
		ol.close();
		tl.close();
	}
	

	@Override
	public void reconnect() {
		close();
		connect();
	}
	
	
	@Override
	public Trade getTrade(String orderId) throws TradeNotFoundException {
		Trade trade = trades.get(orderId);
		if(trade == null)
			throw new TradeNotFoundException();
		return trade;
	}

	@Override
	public Order getOrder(String orderId) throws TradeNotFoundException {
		Order order = orders.get(orderId);
		if(order == null)
			throw new TradeNotFoundException();
		return order;
	}
	
	@Override
	public Trade getTrade(Pair pair) throws TradeNotFoundException {
		for(Trade trade: trades.values()){
			if(trade.getPair() == pair)
				return trade;
		}
		throw new TradeNotFoundException();
	}
	
	@Override
	public Order getOrder(Pair pair) throws TradeNotFoundException{
		for(Order order: orders.values()){
			if(order.getPair() == pair)
				return order;
		}
		throw new TradeNotFoundException();
	}
	
//	@Override
//	public double getStopSize(String orderId) throws TradeNotFoundException {
//		Trade trade = trades.get(orderId);
//		if(trade == null){
//			Order order = orders.get(orderId);
//			if(order == null)
//				throw new TradeNotFoundException();
//			return order.getStopPrice();
//		}
//		else{
//			return trade.getStopPrice();
//		}
//	}

	@Override
	public TradingStatus getTradingStatus(String orderId) {
		
		if(trades.get(orderId) == null){
			if(orders.get(orderId) == null)
				return TradingStatus.NOT_FOUND;
			return TradingStatus.WAITING;
		}
		else{
			return TradingStatus.OPEN;
		}
	}

	
	class OrdersListener implements IO2GTableListener{
		
		void connect(){
			fx.getTable(O2GTableType.ORDERS).subscribeUpdate(O2GTableUpdateType.INSERT, this);
			fx.getTable(O2GTableType.ORDERS).subscribeUpdate(O2GTableUpdateType.DELETE, this);
			fx.getTable(O2GTableType.ORDERS).subscribeUpdate(O2GTableUpdateType.UPDATE, this);
			for(Order o: fx.ordersTable.getAllOrders())
				orders.put(o.getId(), o);
		}
	
		@Override
		public void onAdded(String rowId, O2GRow row) {
			O2GOrderTableRow orderRow = (O2GOrderTableRow)row;
//			Logger.debug("adding new order of type " + orderRow.getType());
			Order order = FxcmUtils.getOrder(orderRow);
			orders.put(order.getId(), order);
		}
	
		@Override
		public void onChanged(String rowId, O2GRow row) {
			O2GOrderTableRow orderRow = (O2GOrderTableRow)row;
			Order order = orders.get(orderRow.getOrderID());
			order.updateStopPrice(orderRow.getStop());
			order.updateLots(orderRow.getAmount()/1000);
			
		}
	
		@Override
		public void onDeleted(String rowId, O2GRow row) {
			O2GOrderTableRow orderRow = (O2GOrderTableRow)row;
			orders.remove(orderRow.getOrderID());
		}
	
		@Override
		public void onStatusChanged(O2GTableStatus arg0) {
			// TODO Auto-generated method stub
			
		}

		public void close() {
			fx.getTable(O2GTableType.ORDERS).unsubscribeUpdate(O2GTableUpdateType.INSERT, this);
			fx.getTable(O2GTableType.ORDERS).unsubscribeUpdate(O2GTableUpdateType.DELETE, this);
			fx.getTable(O2GTableType.ORDERS).unsubscribeUpdate(O2GTableUpdateType.UPDATE, this);
		}

	}
	
	class TradesListener implements IO2GTableListener{
		
		
		public void connect(){
			fx.getTable(O2GTableType.TRADES).subscribeUpdate(O2GTableUpdateType.INSERT, this);
			fx.getTable(O2GTableType.TRADES).subscribeUpdate(O2GTableUpdateType.DELETE, this);
			fx.getTable(O2GTableType.TRADES).subscribeUpdate(O2GTableUpdateType.UPDATE, this);
			for(Trade t: fx.tradesTable.getAllTrades())
				trades.put(t.getId(), t);
		}

		@Override
		public void onAdded(String arg0, O2GRow row) {
			O2GTradeTableRow tradeRow = (O2GTradeTableRow)row;
			Trade trade = FxcmUtils.getTrade(tradeRow);
			trade.setOpenPrice(tradeRow.getOpenRate());
			trade.setOpenTime(tradeRow.getOpenTime().getTime());
			double initialStop = tradeRow.getStop();
			trade.setInitialStopPrice(initialStop);
			trades.put(trade.getId(), trade);
			Logger.info("new trade added for " + trade.getPair() + " at price " + trade.getOpenPrice());
			if(initialStop != 0)
				Logger.info("setting " + trade.getPair() + " initial stop to " + initialStop);
		}

		@Override
		public void onChanged(String arg0, O2GRow row) {
			O2GTradeTableRow tradeRow = (O2GTradeTableRow)row;
			Trade trade = trades.get(tradeRow.getOpenOrderReqID());
			double stop = tradeRow.getStop();
			if(trade.getInitialStopPrice() == 0 && stop != 0){
				trade.setInitialStopPrice(stop);
				Logger.debug(trade.getPair() + " trade initial stop is " + stop);
			}
				
			trade.updateStopPrice(tradeRow.getStop());
			trade.updateLots(tradeRow.getAmount()/1000);
		}

		@Override
		public void onDeleted(String arg0, O2GRow row) {
			O2GTradeTableRow tradeRow = (O2GTradeTableRow)row;
			String orderId = tradeRow.getOpenOrderReqID();
			Trade trade = trades.get(orderId);
			trades.remove(orderId);
			closedTrades.put(orderId, trade);
		}

		@Override
		public void onStatusChanged(O2GTableStatus arg0) {
			// TODO Auto-generated method stub
			
		}

		public void close() {
			fx.getTable(O2GTableType.TRADES).unsubscribeUpdate(O2GTableUpdateType.INSERT, this);
			fx.getTable(O2GTableType.TRADES).unsubscribeUpdate(O2GTableUpdateType.DELETE, this);
			fx.getTable(O2GTableType.TRADES).unsubscribeUpdate(O2GTableUpdateType.UPDATE, this);
		}

		
	}





	
	
	
}
