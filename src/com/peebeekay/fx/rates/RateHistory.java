package com.peebeekay.fx.rates;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map.Entry;

import com.fxcore2.O2GMarketDataSnapshotResponseReader;
import com.fxcore2.O2GRequest;
import com.fxcore2.O2GRequestFactory;
import com.fxcore2.O2GResponse;
import com.fxcore2.O2GResponseReaderFactory;
import com.fxcore2.O2GSession;
import com.fxcore2.O2GTimeframe;
import com.fxcore2.O2GTimeframeCollection;
import com.peebeekay.fx.listeners.RequestFailedException;
import com.peebeekay.fx.session.SessionManager;
import com.peebeekay.fx.utils.ArrayUtils;
import com.peebeekay.fx.utils.DateUtils;

public class RateHistory {
	
	private static final int DEF_WAIT_FOR = 3;
	private static final int LONG_WAIT_FOR = 10;
	//private static final String ANCIENT_DATE = "2000-01-01 00:00:00";
	private static final int DEF_REQUEST_LENGTH = 1000;
	private static final DateFormat LOG_DF = DateUtils.DATE_FORMAT_STD;
	
	
	public static Collection<Double> getTickData(SessionManager sm, String pair, int lastN, String type) 
			throws IllegalArgumentException, IllegalAccessException, RequestFailedException{
		
		O2GMarketDataSnapshotResponseReader marketSnapshotReader = null;
		marketSnapshotReader = getData(sm, pair, "t1", null, null, lastN, DEF_WAIT_FOR);
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
	
	public static LinkedHashMap<Calendar, double[]> getTickData(SessionManager sm, String pair, 
			Calendar startTime, Calendar endTime){
		LinkedHashMap<Calendar, double[]> values = new LinkedHashMap<Calendar, double[]>();
		Calendar endChunk = null;
		LinkedHashMap<Calendar, double[]> chunk;
		try {
			chunk = getMap(getData(sm, pair, "t1", startTime, endTime, DEF_REQUEST_LENGTH, 10), true);
		} catch (RequestFailedException e) {
			return values;
		}
		values.putAll(chunk);
		
		endChunk = ArrayUtils.getLastEntry(chunk).getKey();
		while(endChunk.after(startTime)){
			try {
				chunk = getMap(getData(sm, pair, "t1", startTime, endChunk, DEF_REQUEST_LENGTH, 10), true); // throws exc
				values.putAll(chunk);
				endChunk = ArrayUtils.getLastEntry(chunk).getKey();
			} catch (RequestFailedException e) {
				return values;
			}
		}
		return values;
	}
	
	
	public static ArrayList<ArrayList<Double>> getSnapshot(SessionManager sm, String pair, String interval,
			Calendar startTime, Calendar endTime) throws RequestFailedException{
		O2GMarketDataSnapshotResponseReader snapshotReader = getData(sm, pair, interval, startTime, endTime, DEF_REQUEST_LENGTH, DEF_WAIT_FOR);
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
			String startTimeString, String endTimeString) throws ParseException, IllegalArgumentException, IllegalAccessException, RequestFailedException{
		
		Calendar startTime = Calendar.getInstance(); 
		startTime.setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)).parse(startTimeString));

		Calendar endTime = Calendar.getInstance();
		endTime.setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)).parse(endTimeString));

		return getSnapshotMap(sm, pair, interval, startTime, endTime);
		
	}
	
	public static LinkedHashMap<Calendar, double[]> getSnapshotMap(SessionManager sm, String pair, String interval, 
			Calendar startTime, Calendar endTime) throws IllegalArgumentException, IllegalAccessException, RequestFailedException{

		O2GMarketDataSnapshotResponseReader snapshotReader = getData(sm, pair, interval, startTime, endTime, DEF_REQUEST_LENGTH, DEF_WAIT_FOR);
		return getMap(snapshotReader, false);
		
	}
	
	public static LinkedHashMap<Calendar, double[]> getSnapshotMapGreedy(SessionManager sm, String pair, String interval,
			Calendar startTime, Calendar endTime) throws IllegalArgumentException, IllegalAccessException, RequestFailedException{
		O2GMarketDataSnapshotResponseReader snapshotReader = getData(sm, pair, interval, startTime, endTime, DEF_REQUEST_LENGTH, LONG_WAIT_FOR);
		return getMap(snapshotReader, false);
	}
	
	private static O2GMarketDataSnapshotResponseReader getData(SessionManager sm, String pair, String interval,
			Calendar startTime, Calendar endTime, int lastN, int waitForSeconds) throws RequestFailedException{
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
		O2GResponse response = sm.responseListener.getResponse(requestID, waitForSeconds, "data request for " + pair 
				+ ": " + DateUtils.calToString(startTime,LOG_DF) + " to " + DateUtils.calToString(endTime, LOG_DF));
		if (response == null){
			throw new RequestFailedException("there is no " + interval + " data available for " 
					+ DateUtils.dateToString(startTime.getTime()) + " to " + DateUtils.dateToString(endTime.getTime()));
		}
		return readerFactory.createMarketDataSnapshotReader(response);
	}
	
	
	/**
	 * 
	 * @param reader
	 * @return reversed list, so it is sorted from earliest to latest
	 */
	private static LinkedHashMap<Calendar, double[]> getMap(O2GMarketDataSnapshotResponseReader reader, boolean reverse){
		LinkedHashMap<Calendar, double[]> res = new LinkedHashMap<Calendar, double[]>();
		if(reverse){
			for (int i = reader.size()-1; i >= 0; i--) {
				res.put(reader.getDate(i), AskBid.extract(reader, i));
			}
		}
		else{
			for (int i=0; i < reader.size(); i++) {
				res.put(reader.getDate(i), AskBid.extract(reader, i));
			}
		}
		return res;
	}

	
	static class AskBid{
		static double[] extract(O2GMarketDataSnapshotResponseReader reader, int rowNum) {
			return new double[]{reader.getAsk(rowNum), reader.getBid(rowNum)};
		}
	}
	
	
	
	
	
	

}
