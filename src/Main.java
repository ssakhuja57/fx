import session.SessionManager;
import strategies.spike.SpikeTraderUI;


public class Main {
	
	public static void main(String[] args){
		
		//SessionManager sm = new SessionManager("D172741206001", "1008", "Demo", "2743608", "2743608");
		//SessionManager sm = new SessionManager("D26728250001", "6303", "Demo", "722858", "722858");
		//System.out.println(sm.orders.getTradeIDs("GBP/AUD", Constants.Buy));
		//System.out.println(sm.trades.getTradeIDs("GBP/AUD", Constants.Buy));
		//System.out.println(sm.closedTrades.getTradeIDs("GBP/AUD", Constants.Buy));
		//sm.tradesTable.printTable();
		//sm.closedTrades.printTable();
		//sm.offersTable.printTable();
		//sm.ordersTable.printTable();
		//sm.accountsTable.printTable();
		//SpikeTraderUI ui = new SpikeTraderUI();
		try {
			//System.out.println(sm.createMarketOrder("EUR/AUD", Constants.Buy, 1000));
			//sm.createEntryStopOrder("GBP/AUD", Constants.Buy, 1000, 1.93613, 2, true);
			//PositionActions.createMarketOrder(sm, "02730876", "EUR/USD", Constants.Buy, 1000, new ResponseListener());
			//System.out.println(sm.closeTrade("EUR/AUD", Constants.Buy));
			//sm.cancelOrder("GBP/AUD", Constants.Buy);
			//sm.createOpposingOCOEntryOrdersWithStops("EUR/GBP", 1000, 15, 5, true);
			//sm.createOpposingOCOEntryOrdersWithStops("EUR/USD", 1000, 20, 5, true);
			//sm.adjustOpposingOCOEntryOrders("EUR/USD", 1);
			//PositionActions.removeAllPairSubscriptions(sm, new ResponseListener());
			//sm.cancelAllOCOOrders();
			//SpikeTrader st = new SpikeTrader(sm, "USD", "2015-01-01 10:00");
//			for (double d : RateHistory.getTickData(sm, "EUR/JPY", 10, "sell")){
//				System.out.println(d);
//			}
			SpikeTraderUI ui = new SpikeTraderUI();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			//sm.close();
		}
		
		
	}
}
	
	
	
	
	

