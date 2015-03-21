package rates;

import java.util.Collection;
import java.util.LinkedList;

import session.SessionManager;

import com.fxcore2.O2GMarketDataSnapshotResponseReader;
import com.fxcore2.O2GRequest;
import com.fxcore2.O2GRequestFactory;
import com.fxcore2.O2GResponse;
import com.fxcore2.O2GResponseReaderFactory;
import com.fxcore2.O2GSession;
import com.fxcore2.O2GTimeframe;
import com.fxcore2.O2GTimeframeCollection;

public class RateHistory {
	
	
	public static Collection<Double> getTickData(SessionManager sm, String pair, int lastN, String type){
		System.out.println("creating tick data request for last " + lastN + " ticks of " + pair + " and type " + type);
		O2GSession session = sm.session;
		O2GRequestFactory factory = session.getRequestFactory();
		O2GTimeframeCollection timeFrames = factory.getTimeFrameCollection();
		O2GTimeframe timeFrame = timeFrames.get("t1");
		O2GRequest marketDataRequest = factory.createMarketDataSnapshotRequestInstrument(pair, timeFrame, lastN);
		String requestID = marketDataRequest.getRequestId();
		factory.fillMarketDataSnapshotRequestTime(marketDataRequest, null, null, true);
		session.sendRequest(marketDataRequest);
		O2GResponseReaderFactory readerFactory = session.getResponseReaderFactory();
		O2GResponse response = sm.responseListener.getResponse(requestID, 3);
		O2GMarketDataSnapshotResponseReader marketSnapshotReader = readerFactory.createMarketDataSnapshotReader(response);
		Collection<Double> res = new LinkedList<Double>();
		if (type == "buy"){
			for (int i = marketSnapshotReader.size()-1; i >= 0; i--) { //add in reverse so oldest rate is at beginning of list
				res.add(marketSnapshotReader.getAsk(i));
			}
		}
		else if(type == "sell"){
			for (int i = marketSnapshotReader.size()-1; i >= 0; i--) {
				res.add(marketSnapshotReader.getBid(i));
			}
		}
		else if(type == "high"){
			for (int i = marketSnapshotReader.size()-1; i >= 0; i--) {
				res.add(marketSnapshotReader.getAskHigh(i));
			}
		}
		else if(type == "low"){
			for (int i = marketSnapshotReader.size()-1; i >= 0; i--) {
				res.add(marketSnapshotReader.getBidLow(i));
			}
		}
		return res;

	}
	
	
	
	
	

}
