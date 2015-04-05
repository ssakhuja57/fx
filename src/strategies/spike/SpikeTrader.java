package strategies.spike;

import info.Pairs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import rates.RateCollector;
import rates.RateTools;
import session.SessionHolder;
import session.SessionManager;

import com.fxcore2.Constants;

public class SpikeTrader implements SessionHolder{
	
	private String[] currencies;
	private Calendar eventDate;
	
	private SessionManager sm;
	private boolean isActive;
	private ArrayList<String> pairs;
	
	private int defSpikeBuffer = 10;
	private int defStopBuffer = 9;
	
	private int spikeBufferFloor = 10;
	private int spikeBufferCeiling = 18;
	
	private boolean dataCollect = false;
	private int dataCollectLength = 1800;
	
	private double accountUtilization;
	
	private boolean autoStart;
	private int autoStartBefore;
	private Timer autoStartTimer;
	private Calendar autoStartDate;
	
	private int expireAfter;
	private Timer expirationTimer;
	private Calendar expirationDate;
	
	private boolean recalibrate; // whether to use recalibrator
	private Timer recalibrator;
	private int recalibratorFreq; //frequency in seconds at which to recalibrate orders
	private int recalibrateUntil; //seconds before eventDate to stop recalibrating orders
	private HashMap<String, Boolean> recalibrateParams = new HashMap<String, Boolean>();
	
	private Timer dataCollector;
	private HashMap<String, double[]> data = new HashMap<String, double[]>();
	private HashMap<String, RateCollector> rateCollectors = new HashMap<String, RateCollector>();
	private HashMap<String, Integer[]> params = new HashMap<String, Integer[]>(); //amount, spike buffer, stop buffer
	
	
	public SpikeTrader(SessionManager sm, String[] currencies, String eventDate_string, double accountUtilization,
			boolean autoStart, int autoStartBefore, int expireAfter, boolean autoRecalibrate, int recalibratorFreq, 
			int recalibrateUntil){
		this.sm = sm;
		this.currencies = currencies;
		try {
			this.eventDate = Calendar.getInstance();
			this.eventDate.setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)).parse(eventDate_string));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.accountUtilization = accountUtilization;
		
		this.autoStart = autoStart;
		this.autoStartBefore = autoStartBefore;
		autoStartDate = Calendar.getInstance();
		autoStartDate.setTime(eventDate.getTime());
		autoStartDate.add(Calendar.SECOND, -autoStartBefore);
		
		this.expireAfter = expireAfter;
		this.expirationDate = Calendar.getInstance();
		expirationDate.setTime(eventDate.getTime());
		expirationDate.add(Calendar.SECOND, expireAfter);
		
		
		this.recalibrate = autoRecalibrate;
		this.recalibratorFreq = recalibratorFreq;
		this.recalibrateUntil = recalibrateUntil;
		
		pairs = Pairs.getRelatedPairs(currencies);
		
		if(dataCollect){
			dataCollector = new Timer();
			for (String pair:pairs){
				rateCollectors.put(pair, new RateCollector(sm, pair, dataCollectLength, 1));
			}
			
			dataCollector.schedule(new DataCollector(), 0, 1*1000);
		}
		
		for (String pair:pairs){
			recalibrateParams.put(pair, true);
		}
		
		
		expirationTimer = new Timer();
		expirationTimer.schedule(new ExpirationTask(), expirationDate.getTime());
		
		autoStartTimer = new Timer();
		if(autoStart){
			autoStartTimer.schedule(new AutoStartTask(), autoStartDate.getTime());
		}
		
		
	}
	
	private class AutoStartTask extends TimerTask{
		@Override
		public void run(){
			System.out.println("reached auto start time, placing orders");
			if(params.size() == 0){
				recalculateParams();
			}
			start();
			autoStartTimer.cancel();
		}
	}
	
	private class ExpirationTask extends TimerTask{
		@Override
		public void run() {
			System.out.println("reached expiration time of " + expireAfter + " seconds after Event Date");
			stop();
			close();
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
			if (dataCollect){
				for (String pair: pairs){
					if(recalibrateParams.get(pair)){
						int spikeBuffer = (int)rateCollectors.get(pair).getMaxWindowRange();
						System.out.println(pair + ": buffer calculated = " + spikeBuffer);
						if(spikeBuffer < spikeBufferFloor) spikeBuffer = spikeBufferFloor;
						else if(spikeBuffer > spikeBufferCeiling) spikeBuffer = spikeBufferCeiling;
						setParam(pair, 1, spikeBuffer);
					}
				}
			}
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
			try{
				recalibrator.cancel();
			} catch(NullPointerException e){
				//
			}
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
			e.printStackTrace();
		}
	}
	
	public void printIsRunning(){
		System.out.println("this spike trader instance is currently running, you must cancel before you" +
				" can make changes");
	}
	
	
	public String[] getCurrencies(){
		return currencies;
	}
	
	public String getEventDate(){
		return eventDate.getTime().toString();
	}
	
	public boolean autoStartEnabled(){
		return autoStart;
	}
	
	public String getAutoStartDate(){
		return autoStartDate.getTime().toString();
	}
	
	public String getExpirationDate(){
		return expirationDate.getTime().toString();
	}
	
	public boolean recalibratorEnabled(){
		return this.recalibrate;
	}
	
	public String getRecalibrateUntil(){
		Calendar res = Calendar.getInstance();
		res.setTime(eventDate.getTime());
		res.add(Calendar.SECOND, -recalibrateUntil);
		return res.getTime().toString();
	}
	
	public double getAccountUtilization(){
		return this.accountUtilization;
	}
	
	
	public ArrayList<String> getPairs(){
		return (ArrayList<String>)pairs.clone();
	}
	
	public HashMap<String, Integer[]> getParams(){
		return (HashMap<String, Integer[]>)params.clone();
	}
	
	public void recalculateParams(){
		sm.updateMarginsReqs();
		try{
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		double accountBalance = sm.accountsTable.getBalance(sm.getAccountID(1));
//		double sum_pc_mmr = 0;
		double sum_mmr = 0;
		
		for(String pair: pairs){
//			sum_pc_mmr += (sm.offersTable.getPipCost(pair)/sm.getMarginReqs(pair)[0]);
			sum_mmr += sm.getMarginReqs(pair)[0];
		}
		//int pairsCount = pairs.size();
		
		for (String pair: pairs){
			System.out.println("calculating values for " + pair);
			//double pipCost = sm.offersTable.getPipCost(pair);
			//int lots = (int)((accountBalance*accountUtilization*pipCost)/(Math.pow(mmr, 2.0)*sum_pc_mmr));
			int lots = (int)((accountBalance*accountUtilization)/(sum_mmr));
			System.out.println("setting lots to " + lots + "K");
			
			int spikeBuffer = defSpikeBuffer;
			if(dataCollect){
				int buyDiff = (int)data.get(pair)[2];
				int sellDiff = (int)data.get(pair)[5];
				if(buyDiff > defSpikeBuffer || sellDiff > defSpikeBuffer){
					if(sellDiff > buyDiff){
						spikeBuffer = sellDiff;
						System.out.println("setting spike buffer to sell diff of " + spikeBuffer);
					}
					else{
						spikeBuffer = buyDiff;
						System.out.println("setting spike buffer to buy diff of " + spikeBuffer);
					}
				}
				else{
					System.out.println("setting spike buffer to default value of " + spikeBuffer);
				}
			}
			else{
				System.out.println("setting spike buffer to default value of " + spikeBuffer);
			}
			
			int stopBuffer = defStopBuffer;
			//System.out.println("setting stop buffer to default of " + stopBuffer);
			
			setParams(pair, lots, spikeBuffer, stopBuffer);
		}
	}
	
	public boolean isActive(){
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
	
	private void setParam(String pair, int index, int value){
		params.get(pair)[index] = value;
	}
	
	
	public void setParams(String pair, int lots, int spikeBuffer, int stopBuffer){
		if(isActive){
			printIsRunning();
			return;
		}
		System.out.println("Setting parameters for " + pair + ": lots=" + lots + "K, SpikeBuffer=" 
				+ spikeBuffer + "pips, StopBuffer=" + stopBuffer + "pips");
		params.put(pair, new Integer[]{lots*1000, spikeBuffer, stopBuffer});
	}
	
	public void setRecalibrationOptions(String pair, boolean active){
		recalibrateParams.put(pair, active);
	}
	
	public void start(){
		if (!isActive){
			isActive = true;
			placeAllOrders();
			startRecalibrator();
		}
		else{
			printIsRunning();
		}
	}
	
	public void stop(){
		cancelAllOrders();
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cancelAllOrders();
		stopRecalibrator();
		isActive = false;
	}
	
	@Override
	public void close(){
		autoStartTimer.cancel();
		cancelAllOrders();
		try{
			Thread.currentThread().sleep(500);
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		cancelAllOrders();
		stopRecalibrator();
		expirationTimer.cancel();
		if(dataCollect){
			dataCollector.cancel();
			for (String pair: pairs){
				rateCollectors.get(pair).end();
			}
		}
		try{
			Thread.currentThread().sleep(3000); // wait for cleanup to finish
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally{
			sm.close();
		}
	}


}
