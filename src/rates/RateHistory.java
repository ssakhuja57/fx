package rates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;

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
		O2GSession session = sm.session;
		O2GRequestFactory factory = session.getRequestFactory();
		O2GTimeframeCollection timeFrames = factory.getTimeFrameCollection();
		O2GTimeframe timeFrame = timeFrames.get("t1");
		O2GRequest marketDataRequest = factory.createMarketDataSnapshotRequestInstrument(pair, timeFrame, lastN);
		String requestID = marketDataRequest.getRequestId();
		sm.responseListener.addRequestID(requestID);
		factory.fillMarketDataSnapshotRequestTime(marketDataRequest, null, null, true);
		session.sendRequest(marketDataRequest);
		O2GResponseReaderFactory readerFactory = session.getResponseReaderFactory();
		O2GResponse response = sm.responseListener.getResponse(requestID, 5, type + " data request for " + pair + " for last " + lastN + " ticks");
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
	
	public static LinkedHashMap<Calendar, double[]> getSnapshot(SessionManager sm, String pair, String interval, 
			String startTime, String endTime) throws ParseException{
		Calendar start = Calendar.getInstance(); 
		start.setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)).parse(startTime));
		Calendar end = Calendar.getInstance();
		end.setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)).parse(endTime));
		O2GRequestFactory reqFactory = sm.session.getRequestFactory();
		O2GTimeframeCollection timeFrames = reqFactory.getTimeFrameCollection();
		O2GTimeframe timeFrame = timeFrames.get(interval);
		O2GRequest dataRequest = reqFactory.createMarketDataSnapshotRequestInstrument(pair, timeFrame, 1000);
		reqFactory.fillMarketDataSnapshotRequestTime(dataRequest, start, end, false);
		String requestID = dataRequest.getRequestId();
		sm.responseListener.addRequestID(requestID);
		sm.session.sendRequest(dataRequest);
		O2GResponse dataResponse = sm.responseListener.getResponse(requestID, 10, interval + " request for " + pair);
		O2GResponseReaderFactory resFactory = sm.session.getResponseReaderFactory();
		O2GMarketDataSnapshotResponseReader snapshotReader = resFactory.createMarketDataSnapshotReader(dataResponse);
		LinkedHashMap<Calendar, double[]> res = new LinkedHashMap<Calendar, double[]>();
		for (int i = 0; i < snapshotReader.size(); i++) {
				res.put(snapshotReader.getDate(i), new double[]{snapshotReader.getAsk(i), snapshotReader.getBid(i)});
			}
		return res;
		
	}
	
	
	
	
	

}
