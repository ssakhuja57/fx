package strategies.spike;

import info.Pairs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.fxcore2.Constants;

import rates.RateCollector;
import session.SessionHolder;
import session.SessionManager;

public class SpikeTrader implements SessionHolder{
	
	private String currency;
	private Calendar eventDate;
	
	private SessionManager sm;
	private boolean isActive = false;
	
	private Timer expirationChecker;
	private Calendar expirationDate;
	
	private boolean recalibrate; // use recalibrator
	private Timer recalibrator;
	private int recalibratorFreq = 1; //frequency in seconds at which to recalibrate orders
	private int recalibrateUntil = 30; //seconds before eventDate to stop recalibrating orders
	
	private ArrayList<String> pairs;
	private HashMap<String, RateCollector> rateCollectors = new HashMap<String, RateCollector>();
	private HashMap<String, Integer[]> params = new HashMap<String, Integer[]>(); //spike buffer, 
	
	
	public SpikeTrader(SessionManager sm, String currency, String eventDate_string, int expireAfter,
				boolean autoRecalibrate, int recalibratorFreq, int recalibrateUntil){
		this.sm = sm;
		this.currency = currency;
		try {
			this.eventDate = Calendar.getInstance();
			this.eventDate.setTime((new SimpleDateFormat("YYYY-MM-dd hh:mm", Locale.ENGLISH)).parse(eventDate_string));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.expirationDate = Calendar.getInstance();
		expirationDate.setTime(eventDate.getTime());
		expirationDate.add(Calendar.SECOND, expireAfter);
		
		
		this.recalibrate = autoRecalibrate;
		this.recalibratorFreq = recalibratorFreq;
		this.recalibrateUntil = recalibrateUntil;
		
		pairs = Pairs.getRelatedPairs(currency);
		for (String pair:pairs){
			//rateCollectors.put(pair, new RateCollector(sm, pair));
		}
		
	}
	
	private class ExpirationCheck extends TimerTask{

		@Override
		public void run() {
				expirationChecker.cancel();
		}
	}
	
	private class Recalibrate extends TimerTask{

		@Override
		public void run() {
			if(secondsDiff(Calendar.getInstance(), eventDate) <= recalibrateUntil){
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
		return new SimpleDateFormat("YYYY-MM-dd HH:mm").format(new Date());
	}
	
	@Override
	public void close(){
		stopRecalibrator();
		expirationChecker.cancel();
		sm.close();
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
			sm.adjustOpposingOCOEntryOrders(pair, params.get(pair)[1]);
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
		expirationChecker.schedule(new ExpirationCheck(), expirationDate.getTime());
	}
	
	public void stop(){
		cancelAllOrders();
		stopRecalibrator();
		expirationChecker.cancel();
		isActive = false;
	}


}
