package strategies.spike;

import info.Pairs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import rates.RateCollector;
import session.SessionHolder;
import session.SessionManager;

public class SpikeTrader implements SessionHolder{
	
	private String currency;
	private Date eventDate;
	
	private SessionManager sm;
	
	private boolean recalibrate; // use recalibrator
	private Timer recalibrator;
	private int recalibratorFreq = 1; //frequency in seconds at which to recalibrate orders
	private int recalibrateUntil = 30; //seconds before eventDate to stop recalibrating orders
	
	private ArrayList<String> pairs;
	private HashMap<String, RateCollector> rateCollectors = new HashMap<String, RateCollector>();
	private HashMap<String, Integer[]> params = new HashMap<String, Integer[]>(); //spike buffer, 
	
	
	public SpikeTrader(SessionManager sm, String currency, String eventDate_string,
				boolean autoRecalibrate, int recalibratorFreq, int recalibrateUntil){
		this.sm = sm;
		this.currency = currency;
		try {
			this.eventDate = (new SimpleDateFormat("YYYY-MM-dd hh:mm", Locale.ENGLISH)).parse(eventDate_string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.recalibrate = autoRecalibrate;
		this.recalibratorFreq = recalibratorFreq;
		this.recalibrateUntil = recalibrateUntil;
		
		pairs = Pairs.getRelatedPairs(currency);
		for (String pair:pairs){
			rateCollectors.put(pair, new RateCollector(sm, pair));
		}
		
	}
	
	private class Recalibrate extends TimerTask{

		@Override
		public void run() {
			if (secondsDiff(new Date(), eventDate) <= recalibrateUntil){
				recalibrator.cancel();
			}
			recalibrateAllOrders();
		}
	}
	

	private void startRecalibrator(){
		if(recalibrate){
			recalibrator = new Timer();
			recalibrator.schedule(new Recalibrate(), 3*1000, recalibratorFreq*1000);
		}
	}
	
	private void stopRecalibrator(){
		if(recalibrate){
			recalibrator.cancel();
		}
	}

	private int secondsDiff(Date d1, Date d2){
		return (int) ((d2.getTime()-d1.getTime())/1000);
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
		sm.close();
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
		placeAllOrders();
		startRecalibrator();
	}
	
	public void stop(){
		cancelAllOrders();
		stopRecalibrator();
	}


}
