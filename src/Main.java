import listeners.ResponseListener;
import session.SessionManager;
import actions.PositionActions;


public class Main {
	
	public static void main(String[] args){
		
		SessionManager sm = new SessionManager("D172741206001", "1008", "Demo", "02743608", "02743608");
		//System.out.println(sm.orders.getTradeIDs("GBP/AUD", Constants.Buy));
		//System.out.println(sm.trades.getTradeIDs("GBP/AUD", Constants.Buy));
		//System.out.println(sm.closedTrades.getTradeIDs("GBP/AUD", Constants.Buy));
		//sm.tradesTable.printTable();
		//sm.closedTrades.printTable();
		//sm.offersTable.printTable();
		sm.ordersTable.printTable();
		try {
			//System.out.println(sm.createMarketOrder("EUR/AUD", Constants.Buy, 1000));
			//sm.createEntryStopOrder("GBP/AUD", Constants.Buy, 1000, 1.93613, 2, true);
			//PositionActions.createMarketOrder(sm, "02730876", "EUR/USD", Constants.Buy, 1000, new ResponseListener());
			//System.out.println(sm.closeTrade("EUR/AUD", Constants.Buy));
			//sm.cancelOrder("GBP/AUD", Constants.Buy);
			//sm.createOpposingOCOEntryOrdersWithStops("EUR/GBP", 1000, 15, 5, true);
			//sm.createOpposingOCOEntryOrdersWithStops("EUR/CHF", 1000, 20, 5, true);
			//sm.adjustOpposingOCOEntryOrders("EUR/CHF", 50);
			//PositionActions.removeAllPairSubscriptions(sm, new ResponseListener());
			sm.cancelAllOCOOrders();
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			sm.close();
		}
		
		
	}
}
	
	
	
	
	

