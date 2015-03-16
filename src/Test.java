import listeners.ResponseListener;
import listeners.SessionStatusListener;

import actions.PositionActions;

import com.fxcore2.Constants;
import com.fxcore2.O2GClosedTradeTableRow;
import com.fxcore2.O2GClosedTradesTable;
import com.fxcore2.O2GRequest;
import com.fxcore2.O2GRequestFactory;
import com.fxcore2.O2GResponse;
import com.fxcore2.O2GResponseReaderFactory;
import com.fxcore2.O2GSession;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableManagerMode;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTimeframe;
import com.fxcore2.O2GTimeframeCollection;
import com.fxcore2.O2GTransport;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

        O2GSession session = O2GTransport.createSession();
        SessionStatusListener statusListener = new SessionStatusListener();
        ResponseListener responseListener = new ResponseListener();
        session.subscribeSessionStatus(statusListener);
        session.subscribeResponse(responseListener);
        session.useTableManager(O2GTableManagerMode.YES, null);
        session.login("D172728472001", "817", "http://www.fxcorporate.com/Hosts.jsp", "Demo");
        try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        O2GRequestFactory factory = session.getRequestFactory();
        O2GTimeframeCollection timeFrames = factory.getTimeFrameCollection();
        O2GTimeframe timeFrame = timeFrames.get("m30");
        O2GRequest marketDataRequest = factory.createMarketDataSnapshotRequestInstrument("EUR/USD", timeFrame, 20);
        session.sendRequest(marketDataRequest);
        O2GResponseReaderFactory readFactory = session.getResponseReaderFactory();
//        try {
//			Thread.sleep(3*1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        O2GMarketDataSnapshotResponseReader marketSnapshotReader = readFactory.createMarketDataSnapshotReader(responseListener.response);
//        for (int i = 0; i < marketSnapshotReader.size(); i++) {
//        	{
//        	    System.out.println(
//        	    					marketSnapshotReader.getDate(i).getTime().toString() + " | " 
//        	    					+ marketSnapshotReader.getBidOpen(i) + " | "
//        	    					+ marketSnapshotReader.getBidHigh(i) + " | "
//        	    					+ marketSnapshotReader.getBidLow(i) + " | "
//        	    					+ marketSnapshotReader.getBidClose(i) + " | " 
//        	    					//+ marketSnapshotReader.getVolume(i)
//        	    					);
//        	}
//        	}
//        
        try {
			//O2GResponse opened = PositionActions.openPosition(session, "02730876", "1", Constants.Buy, 100, responseListener);
			Thread.sleep(1*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        O2GTableManager tableManager = session.getTableManager();
        //O2GTradesTable tradesTable = (O2GTradesTable)tableManager.getTable(O2GTableType.TRADES);
        //O2GTradeTableRow row = tradesTable.getRow(0);
        //System.out.println(row.getBuySell());
        
        O2GClosedTradesTable closedTradesTable= (O2GClosedTradesTable)tableManager.getTable(O2GTableType.CLOSED_TRADES);
        O2GClosedTradeTableRow row = closedTradesTable.getRow(0);
        System.out.println(row.getCloseTime().getTime().toString() + " ----- " + row.getOfferID() + " ----- " + 
        		row.getOpenRate() + " " + row.getCloseRate());
        
        session.logout();
        session.unsubscribeSessionStatus(statusListener);
        session.unsubscribeResponse(responseListener);
        session.dispose();

	}

}
