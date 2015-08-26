package com.peebeekay.fx.brokers.fxcm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.fxcore2.O2GMarketDataSnapshotResponseReader;
import com.fxcore2.O2GRequest;
import com.fxcore2.O2GRequestFactory;
import com.fxcore2.O2GResponse;
import com.fxcore2.O2GResponseReaderFactory;
import com.fxcore2.O2GSession;
import com.fxcore2.O2GTimeframe;
import com.fxcore2.O2GTimeframeCollection;
import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.listeners.RequestFailedException;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.utils.ArrayUtils;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.PairUtils;

public class FxcmRateHistory {
	
	
	private static final int DEF_WAIT_FOR = 3;
	private static final int LONG_WAIT_FOR = 100;
	//private static final String ANCIENT_DATE = "2000-01-01 00:00:00";
	private static final int DEF_REQUEST_LENGTH = 1000;
	private static final String LOG_DF = DateUtils.DATE_FORMAT_STD;
	
	public static Map<Calendar, double[]> getTickData(FxcmSessionManager sm, Pair pair,
			Calendar startTime, Calendar endTime){
		return getAggregatedData(sm, pair, Interval.T, new AskBid(), startTime, endTime);
	}
	
	public static ArrayList<Tick> getTicks(FxcmSessionManager fx, Pair pair, Calendar start, Calendar end){
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		Map<Calendar, double[]> values = getAggregatedData(fx, pair, Interval.T, new AskBid(), start, end);
		for(Entry<Calendar, double[]> entry: values.entrySet())
			ticks.add(new Tick(pair, entry.getKey().getTime(), entry.getValue()[0], entry.getValue()[1]));
		return ticks;
	}
	
	public static Map<Calendar, double[]> getOHLCData(FxcmSessionManager sm, Pair pair, Interval interval,
			Calendar startTime, Calendar endTime){
		return getAggregatedData(sm, pair, interval, new AskBidOHLC(), startTime, endTime);
	}
	
	public static OhlcPrice getOhlcRow(FxcmSessionManager fx, Pair pair, Interval interval, Calendar time){
		Calendar start = Calendar.getInstance(); start.setTime(time.getTime()); start.add(Calendar.MINUTE, -interval.minutes);
		Calendar end = Calendar.getInstance(); end.setTime(time.getTime()); end.add(Calendar.MINUTE, interval.minutes);
//		for(Calendar c: getOHLCData(fx, pair, interval, start, end).keySet())
//			Logger.debug(c.getTime().toString());
//		Logger.debug("desired: " + time.getTime().toString());
		double[] row = getOHLCData(fx, pair, interval, start, end).get(time);
		return new OhlcPrice(pair, time.getTime(), interval, 
				row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7]);
//		return null;
	}
	
	public static ArrayList<OhlcPrice> getOhlcRows(FxcmSessionManager fx, Pair pair, Interval interval,
			Calendar start, Calendar end){
		ArrayList<OhlcPrice> prices = new ArrayList<OhlcPrice>();
		Map<Calendar, double[]> values = getAggregatedData(fx, pair, interval, new AskBidOHLC(), start, end);
		for(Entry<Calendar, double[]> entry: values.entrySet()){
			double[] row = entry.getValue();
			prices.add(new OhlcPrice(pair, entry.getKey().getTime(), interval, 
					row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7]));
		}
		return prices;
	}
	
	public static ArrayList<ArrayList<Double>> getSnapshot(FxcmSessionManager sm, Pair pair, Interval interval,
			Calendar startTime, Calendar endTime) throws RequestFailedException{
		O2GMarketDataSnapshotResponseReader snapshotReader = getData(sm, pair, interval, startTime, endTime, 
				DEF_REQUEST_LENGTH, DEF_WAIT_FOR);
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
	
	public static LinkedHashMap<Calendar, double[]> getSnapshotMap(FxcmSessionManager sm, Pair pair, Interval interval, 
			String startTimeString, String endTimeString) throws ParseException, IllegalArgumentException, IllegalAccessException, RequestFailedException{
		
		Calendar startTime = Calendar.getInstance(); 
		startTime.setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)).parse(startTimeString));

		Calendar endTime = Calendar.getInstance();
		endTime.setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)).parse(endTimeString));

		return getSnapshotMap(sm, pair, interval, startTime, endTime);
	}
	
	public static LinkedHashMap<Calendar, double[]> getSnapshotMap(FxcmSessionManager sm, Pair pair, Interval interval, 
			Calendar startTime, Calendar endTime) throws IllegalArgumentException, IllegalAccessException, RequestFailedException{

		O2GMarketDataSnapshotResponseReader snapshotReader = getData(sm, pair, interval, startTime, endTime, DEF_REQUEST_LENGTH, DEF_WAIT_FOR);
		return getMap(snapshotReader, new AskBidOHLC(), false);
	}
	
	private static TreeMap<Calendar, double[]> getAggregatedData(FxcmSessionManager sm, Pair pair, Interval interval,
			Extractor extractor, Calendar startTime, Calendar endTime){
		TreeMap<Calendar, double[]> values = new TreeMap<Calendar, double[]>();
		Calendar endChunk = null;
		TreeMap<Calendar, double[]> chunk;
		try {
			chunk = getSortedMap(getData(sm, pair, interval, startTime, endTime, DEF_REQUEST_LENGTH, 10), extractor, true);
		} catch (RequestFailedException e) {
			return values;
		}
		values.putAll(chunk);
		
		endChunk = ArrayUtils.getLastEntry(chunk).getKey();
		while(endChunk.after(startTime)){
			try {
				chunk = getSortedMap(getData(sm, pair, interval, startTime, endChunk, DEF_REQUEST_LENGTH, 10),
						extractor, true);
				values.putAll(chunk);
				if(chunk.size() == 1)
					break;
				endChunk = chunk.firstKey();
				//Logger.debug(DateUtils.calToString(endChunk) + " size: " + chunk.size());
			} catch (RequestFailedException e) {
				return values;
			}
		}
		return values;
	}
	
	private static O2GMarketDataSnapshotResponseReader getData(FxcmSessionManager sm, Pair pair, Interval in,
			Calendar startTime, Calendar endTime, int lastN, int waitForSeconds) throws RequestFailedException{
		if(endTime != null && !endTime.after(startTime) && !endTime.equals(startTime)){
			throw new IllegalArgumentException("end time must be after start time");
		}
		Logger.debug("requesting " + pair + " " + in.value + " data for " 
				+ DateUtils.calToString(startTime) + " to " + DateUtils.calToString(endTime));
		O2GSession session = sm.session;
		O2GRequestFactory factory = session.getRequestFactory();
		O2GTimeframeCollection timeFrames = factory.getTimeFrameCollection();
		O2GTimeframe timeFrame = timeFrames.get(in.value);
		O2GRequest marketDataRequest = factory.createMarketDataSnapshotRequestInstrument(PairUtils.insertSlash(pair), timeFrame, lastN);
		String requestID = marketDataRequest.getRequestId();
		sm.responseListener.addRequestID(requestID);
		factory.fillMarketDataSnapshotRequestTime(marketDataRequest, startTime, endTime, true);
		session.sendRequest(marketDataRequest);
		O2GResponseReaderFactory readerFactory = session.getResponseReaderFactory();
		O2GResponse response = sm.responseListener.getResponse(requestID, waitForSeconds, "data request for " + pair 
				+ ": " + DateUtils.calToString(startTime,LOG_DF) + " to " + DateUtils.calToString(endTime, LOG_DF));
		if (response == null){
			throw new RequestFailedException("there is no " + in.value + " data available for " 
					+ DateUtils.dateToString(startTime.getTime()) + " to " + DateUtils.dateToString(endTime.getTime()));
		}
		return readerFactory.createMarketDataSnapshotReader(response);
	}
	
	
	
	/**
	 * 
	 * @param reader
	 * @return reversed list, so it is sorted from earliest to latest
	 */
	private static LinkedHashMap<Calendar, double[]> getMap(O2GMarketDataSnapshotResponseReader reader, 
			Extractor ext, boolean reverse){
		LinkedHashMap<Calendar, double[]> res = new LinkedHashMap<Calendar, double[]>();
		if(reverse){
			for (int i = reader.size()-1; i >= 0; i--) {
				res.put(reader.getDate(i), ext.extract(reader, i));
			}
		}
		else{
			for (int i=0; i < reader.size(); i++) {
				res.put(reader.getDate(i), ext.extract(reader, i));
			}
		}
		return res;
	}
	
	private static TreeMap<Calendar, double[]> getSortedMap(O2GMarketDataSnapshotResponseReader reader,
												Extractor ext, boolean reverse){
		TreeMap<Calendar, double[]> res  = new TreeMap<Calendar, double[]>();
		for(int i=0; i<reader.size(); i++)
		{
			res.put(reader.getDate(i), ext.extract(reader, i));
		}
		return res;
	}
	private interface Extractor{
		double[] extract(O2GMarketDataSnapshotResponseReader reader, int rowNum);
	}

	
	static class AskBid implements Extractor{

		@Override
		public double[] extract(O2GMarketDataSnapshotResponseReader reader, int rowNum) {
			return new double[]{reader.getAsk(rowNum), reader.getBid(rowNum)};
		}
	}
	
	static class AskBidOHLC implements Extractor{
		@Override
		public double[] extract(O2GMarketDataSnapshotResponseReader reader, int rowNum){
			return new double[]{reader.getAskOpen(rowNum), reader.getAskHigh(rowNum),
								reader.getAskLow(rowNum), reader.getAskClose(rowNum),
								reader.getBidOpen(rowNum), reader.getBidHigh(rowNum),
								reader.getBidLow(rowNum), reader.getBidClose(rowNum)};
		}
	}
	
}

	
	
	
	
	

