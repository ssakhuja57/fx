package strategies.spike;

import info.Pairs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.fxcore2.Constants;

import rates.RateCollector;
import rates.RateTools;
import session.SessionHolder;
import session.SessionManager;

public class SpikeTrader implements SessionHolder{
	
	private String currency;
	private Calendar eventDate;
	
	private SessionManager sm;
	private boolean isActive;
	private ArrayList<String> pairs;
	
	private int expireAfter;
	private Timer expirationChecker;
	private Calendar expirationDate;
	
	private boolean recalibrate; // whether to use recalibrator
	private Timer recalibrator;
	private int recalibratorFreq; //frequency in seconds at which to recalibrate orders
	private int recalibrateUntil; //seconds before eventDate to stop recalibrating orders
	
	private Timer dataCollector;
	private HashMap<String, double[]> data = new HashMap<String, double[]>();
	private HashMap<String, RateCollector> rateCollectors = new HashMap<String, RateCollector>();
	private HashMap<String, Integer[]> params = new HashMap<String, Integer[]>(); //amount, spike buffer, stop buffer
	
	
	public SpikeTrader(SessionManager sm, String currency, String eventDate_string, int expireAfter,
				boolean autoRecalibrate, int recalibratorFreq, int recalibrateUntil){
		this.sm = sm;
		this.currency = currency;
		try {
			this.eventDate = Calendar.getInstance();
			this.eventDate.setTime((new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH)).parse(eventDate_string));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.expireAfter = expireAfter;
		this.expirationDate = Calendar.getInstance();
		expirationDate.setTime(eventDate.getTime());
		expirationDate.add(Calendar.SECOND, expireAfter);
		
		this.recalibrate = autoRecalibrate;
		this.recalibratorFreq = recalibratorFreq;
		this.recalibrateUntil = recalibrateUntil;
		
		pairs = Pairs.getRelatedPairs(currency);
		for (String pair:pairs){
			rateCollectors.put(pair, new RateCollector(sm, pair, 300, 1));
		}
		
		dataCollector = new Timer();
		//dataCollector.schedule(new DataCollector(), 0, 1*1000);
		
		recalculateParams();
		
//		for (String pair: pairs){
//			Integer[] paramrow = params.get(pair);
//			System.out.println(pair + ": " + paramrow[0] + " " + paramrow[1] + " " + paramrow[2]);
//		}
		
		
	}
	
	private class ExpirationTask extends TimerTask{
		@Override
		public void run() {
			System.out.println("reached expiration time of " + expireAfter + " seconds after Event Date");
				stop();
		}
	}
	
	private class DataCollector extends TimerTask{
		@Override
		public void run() {
			for (String pair: pairs){
				RateCollector rc = rateCollectors.get(pair);
				double buyHigh = rc.getHigh("buy", 0);
				double buyLow = rc.getLow("buy", 0);
				double buyDiff = RateTools.convertToPips(buyHigh - buyLow, pair);
				double sellHigh = rc.getHigh("sell", 0);
				double sellLow = rc.getLow("sell", 0);
				double sellDiff = RateTools.convertToPips(sellHigh - sellLow, pair);
				double slope = RateTools.convertToPips(rc.getSlope("buy", 3), pair);
				double stdDev = RateTools.convertToPips(rc.getStdDev("buy"), pair);
				data.put(pair, new double[]{buyLow, buyHigh, buyDiff, sellLow, sellHigh, sellDiff, slope, stdDev});
			}
		}
	}
	
	private class Recalibrate extends TimerTask{
		@Override
		public void run() {
			if(secondsDiff(Calendar.getInstance(), eventDate) <= recalibrateUntil){
				System.out.println("ending recalibrator since " + Calendar.getInstance().getTime().toString() + " is within "
						+ recalibrateUntil + " seconds of Event Date " + eventDate.getTime().toString());
				recalibrator.cancel();
			}
			recalibrateAllOrders();
		}
	}
	
	

	private void startRecalibrator(){
		if(recalibrate){
			recalibrator = new Timer();
			recalibrator.schedule(new Recalibrate(), 2*1000, recalibratorFreq*1000);
		}
	}
	
	private void stopRecalibrator(){
		if(recalibrate){
			recalibrator.cancel();
		}
	}

	private int secondsDiff(Calendar c1, Calendar c2){
		return (int) ((c2.getTimeInMillis()-c1.getTimeInMillis())/1000);
	}
	
	
	private void placeAllOrders(){
		for (String pair: pairs){
			Integer[] pairParams = params.get(pair); 
			sm.createOpposingOCOEntryOrdersWithStops(pair, pairParams[0], pairParams[1], pairParams[2], true);
		}
	}
	
	private void cancelAllOrders(){
		try {
			sm.cancelAllOCOOrders();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public String getCurrency(){
		return currency;
	}
	
	public String getEventDate(){
		//return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(eventDate);
		return eventDate.getTime().toString();
	}
	
	@Override
	public void close(){
		stopRecalibrator();
		expirationChecker.cancel();
		dataCollector.cancel();
		sm.close();
	}
	
	public ArrayList<String> getPairs(){
		return (ArrayList<String>)pairs.clone();
	}
	
	public HashMap<String, Integer[]> getParams(){
		return (HashMap<String, Integer[]>)params.clone();
	}
	
	public void recalculateParams(){
		sm.updateMarginsReqs();
		double accountBalance = sm.accountsTable.getBalance(sm.getAccountID(1));
		double usableMargin = sm.usableMargin;
		double sum_pc_mmr = 0;
		
		for(String pair: pairs){
			sum_pc_mmr += (sm.offersTable.getPipCost(pair)/sm.getMarginReqs(pair)[0]);
		}
		
		for (String pair: pairs){
			double mmr = sm.getMarginReqs(pair)[0];
			double pipCost = sm.offersTable.getPipCost(pair);
			int lots = (int)((accountBalance*usableMargin*pipCost)/(Math.pow(mmr, 2.0)*sum_pc_mmr));
			int spikeBuffer = 15;
			int stopBuffer = 12;
			params.put(pair, new Integer[]{ lots, spikeBuffer, stopBuffer });
		}
	}
	
	public boolean getIsActive(){
		return isActive;
	}
	
	public void subscribeCurrency(){
		for (String pair: pairs){
			sm.setPairSubscription(pair, Constants.SubscriptionStatuses.Tradable);
		}
	}
	
	public void unsubscribeAll(){
		sm.removeAllPairSubscriptions();
	}
	
	public void recalibrateAllOrders(){
		for (String pair: pairs){
			try{
				sm.adjustOpposingOCOEntryOrders(pair, params.get(pair)[1]);
			} catch(NullPointerException e){
				System.out.println("Unable to recalibrate for " + pair + ", can't find OCO orders related to it");
			}
		}
	}
	
	public boolean setParams(String pair, int lots, int spikeBuffer, int stopBuffer){
		System.out.println("Setting parameters for " + pair + ": lots=" + lots + "K, SpikeBuffer=" 
				+ spikeBuffer + "pips, StopBuffer=" + stopBuffer + "pips");
		params.put(pair, new Integer[]{lots*1000, spikeBuffer, stopBuffer});
		return true;
	}
	
	public void start(){
		isActive = true;
		placeAllOrders();
		startRecalibrator();
		expirationChecker = new Timer();
		expirationChecker.schedule(new ExpirationTask(), expirationDate.getTime());
	}
	
	public void stop(){
		cancelAllOrders();
		stopRecalibrator();
		expirationChecker.cancel();
		isActive = false;
	}


}
