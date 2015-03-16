import session.SessionManager;

import com.fxcore2.Constants;


public class Main {
	
	public static void main(String[] args){
		
		SessionManager sm = new SessionManager("D172728472001", "817", "Demo", "02730876", "02730876");
		
		//System.out.println(sm.orders.getTradeIDs("GBP/AUD", Constants.Buy));
		//System.out.println(sm.trades.getTradeIDs("GBP/AUD", Constants.Buy));
		//System.out.println(sm.closedTrades.getTradeIDs("GBP/AUD", Constants.Buy));
		sm.orders.printTable();
		sm.trades.printTable();
		//sm.closedTrades.printTable();
		try {
			//System.out.println(sm.createMarketOrder("EUR/AUD", Constants.Buy, 1000));
			System.out.println(sm.createEntryStopOrder("GBP/AUD", Constants.Buy, 1000, 1.93613, 2, true));
			//PositionActions.createMarketOrder(sm, "02730876", "EUR/USD", Constants.Buy, 1000, new ResponseListener());
			//System.out.println(sm.closeTrade("EUR/AUD", Constants.Buy));
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
	
	
	
	
	

