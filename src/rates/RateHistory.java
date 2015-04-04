package rates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
		
		O2GMarketDataSnapshotResponseReader marketSnapshotReader = null;
		marketSnapshotReader = getData(sm, pair, "t1", null, null, lastN);
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
	
	public static ArrayList<ArrayList<Double>> getSnapshot(SessionManager sm, String pair, String interval,
			Calendar startTime, Calendar endTime){
		O2GMarketDataSnapshotResponseReader snapshotReader = getData(sm, pair, interval, startTime, endTime, 1000);
		ArrayList<Double> buys = new ArrayList<Double>();
		ArrayList<Double> sells = new ArrayList<Double>();
		for (int i = snapshotReader.size()-1; i >= 0; i--) { //add in reverse so oldest rate is at beginning of list
			buys.add(snapshotReader.getAsk(i));
			sells.add(snapshotReader.getBid(i));
		}
		ArrayList<ArrayList<Double>> res = new ArrayList<ArrayList<Double>>();
		res.add(buys);
		res.add(sells);
		return res;
		
	}
	
	public static LinkedHashMap<Calendar, double[]> getSnapshotMap(SessionManager sm, String pair, String interval, 
			String startTimeString, String endTimeString) throws ParseException{
		
		Calendar startTime = Calendar.getInstance(); 
		startTime.setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)).parse(startTimeString));

		Calendar endTime = Calendar.getInstance();
		endTime.setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)).parse(endTimeString));

		return getSnapshotMap(sm, pair, interval, startTime, endTime);
		
	}
	
	public static LinkedHashMap<Calendar, double[]> getSnapshotMap(SessionManager sm, String pair, String interval, 
			Calendar startTime, Calendar endTime){

		O2GMarketDataSnapshotResponseReader snapshotReader = getData(sm, pair, interval, startTime, endTime, 1000);
		LinkedHashMap<Calendar, double[]> res = new LinkedHashMap<Calendar, double[]>();
		for (int i = snapshotReader.size()-1; i >= 0; i--) { //add in reverse so oldest rate is at beginning of list
				res.put(snapshotReader.getDate(i), new double[]{snapshotReader.getAsk(i), snapshotReader.getBid(i)});
			}
		return res;
		
	}
	
	private static O2GMarketDataSnapshotResponseReader getData(SessionManager sm, String pair, String interval,
			Calendar startTime, Calendar endTime, int lastN){
		if(endTime != null && !endTime.after(startTime)){
			throw new IllegalArgumentException("end time must be after start time");
		}
		O2GSession session = sm.session;
		O2GRequestFactory factory = session.getRequestFactory();
		O2GTimeframeCollection timeFrames = factory.getTimeFrameCollection();
		O2GTimeframe timeFrame = timeFrames.get(interval);
		O2GRequest marketDataRequest = factory.createMarketDataSnapshotRequestInstrument(pair, timeFrame, lastN);
		String requestID = marketDataRequest.getRequestId();
		sm.responseListener.addRequestID(requestID);
		factory.fillMarketDataSnapshotRequestTime(marketDataRequest, startTime, endTime, true);
		session.sendRequest(marketDataRequest);
		O2GResponseReaderFactory readerFactory = session.getResponseReaderFactory();
		O2GResponse response = sm.responseListener.getResponse(requestID, 2, "data request for " + pair ); //+ " --- " + startTime.getTime().toString() + " --- " + endTime.getTime().toString());
		return readerFactory.createMarketDataSnapshotReader(response);
	}
	
	
	
	
	

}
