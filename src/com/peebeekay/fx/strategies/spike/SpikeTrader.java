package com.peebeekay.fx.strategies.spike;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.fxcore2.Constants;
import com.peebeekay.fx.brokers.fxcm.FxcmSessionManager;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.rates.RateCollector;
import com.peebeekay.fx.session.SessionHolder;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.PairUtils;
import com.peebeekay.fx.utils.RateUtils;

public class SpikeTrader implements SessionHolder{
	
	private String[] currencies;
	private Calendar eventDate;
	
	private FxcmSessionManager sm;
	private boolean isActive;
	private ArrayList<Pair> pairs;
	
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
	private HashMap<Pair, Boolean> recalibrateParams = new HashMap<Pair, Boolean>();
	
	private Timer dataCollector;
	private HashMap<Pair, double[]> data = new HashMap<Pair, double[]>();
	private HashMap<Pair, RateCollector> rateCollectors = new HashMap<Pair, RateCollector>();
	private HashMap<Pair, Integer[]> params = new HashMap<Pair, Integer[]>(); //amount, spike buffer, stop buffer
	
	
	public SpikeTrader(FxcmSessionManager sm, String[] currencies, String eventDate_string, double accountUtilization,
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
		
		pairs = PairUtils.getRelatedPairs(currencies);
		
		if(dataCollect){
			dataCollector = new Timer();
			for (Pair pair:pairs){
				rateCollectors.put(pair, new RateCollector(sm, pair, dataCollectLength, 1));
			}
			
			dataCollector.schedule(new DataCollector(), 0, 1*1000);
		}
		
		for (Pair pair:pairs){
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
			Logger.info("reached auto start time, placing orders");
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
			Logger.info("reached expiration time of " + expireAfter + " seconds after Event Date");
			stop();
			close();
		}
	}
	
	private class DataCollector extends TimerTask{
		@Override
		public void run() {
			for (Pair pair: pairs){
				RateCollector rc = rateCollectors.get(pair);
				double buyHigh = rc.getHigh("buy", 0);
				double buyLow = rc.getLow("buy", 0);
				double buyDiff = RateUtils.convertToPips(buyHigh - buyLow, pair);
				double sellHigh = rc.getHigh("sell", 0);
				double sellLow = rc.getLow("sell", 0);
				double sellDiff = RateUtils.convertToPips(sellHigh - sellLow, pair);
				double slope = RateUtils.convertToPips(rc.getSlope("buy", 3), pair);
				double stdDev = RateUtils.convertToPips(rc.getStdDev("buy"), pair);
				data.put(pair, new double[]{buyLow, buyHigh, buyDiff, sellLow, sellHigh, sellDiff, slope, stdDev});
			}
		}
	}
	
	private class Recalibrate extends TimerTask{
		@Override
		public void run() {
			if(DateUtils.secondsDiff(Calendar.getInstance(), eventDate) <= recalibrateUntil){
				Logger.info("ending recalibrator since " + Calendar.getInstance().getTime().toString() + " is within "
						+ recalibrateUntil + " seconds of Event Date " + eventDate.getTime().toString());
				recalibrator.cancel();
			}
			recalibrateAllOrders();
			if (dataCollect){
				for (Pair pair: pairs){
					if(recalibrateParams.get(pair)){
						int spikeBuffer = (int)rateCollectors.get(pair).getMaxWindowRange();
						Logger.debug(pair + ": buffer calculated = " + spikeBuffer);
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
	
	
	private void placeAllOrders(){
		for (Pair pair: pairs){
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
	
	public void logIsRunning(){
		Logger.error("this spike trader instance is currently running, you must cancel before you" +
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
	
	
	public ArrayList<Pair> getPairs(){
		return (ArrayList<Pair>)pairs.clone();
	}
	
	public HashMap<Pair, Integer[]> getParams(){
		return (HashMap<Pair, Integer[]>)params.clone();
	}
	
	public void recalculateParams(){
		sm.updateMarginsReqs();
		try{
			Thread.sleep(1000);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		double accountBalance = sm.accountsTable.getBalance(sm.getAccountID(1));
//		double sum_pc_mmr = 0;
		double sum_mmr = 0;
		
		for(Pair pair: pairs){
//			sum_pc_mmr += (sm.offersTable.getPipCost(pair)/sm.getMarginReqs(pair)[0]);
			sum_mmr += sm.getMarginReqs(pair)[0];
		}
		//int pairsCount = pairs.size();
		
		for (Pair pair: pairs){
			Logger.debug("calculating values for " + pair);
			//double pipCost = sm.offersTable.getPipCost(pair);
			//int lots = (int)((accountBalance*accountUtilization*pipCost)/(Math.pow(mmr, 2.0)*sum_pc_mmr));
			int lots = (int)((accountBalance*accountUtilization)/(sum_mmr));
			Logger.debug("setting lots to " + lots + "K");
			
			int spikeBuffer = defSpikeBuffer;
			if(dataCollect){
				int buyDiff = (int)data.get(pair)[2];
				int sellDiff = (int)data.get(pair)[5];
				if(buyDiff > defSpikeBuffer || sellDiff > defSpikeBuffer){
					if(sellDiff > buyDiff){
						spikeBuffer = sellDiff;
						Logger.debug("setting spike buffer to sell diff of " + spikeBuffer);
					}
					else{
						spikeBuffer = buyDiff;
						Logger.debug("setting spike buffer to buy diff of " + spikeBuffer);
					}
				}
				else{
					Logger.debug("setting spike buffer to default value of " + spikeBuffer);
				}
			}
			else{
				Logger.debug("setting spike buffer to default value of " + spikeBuffer);
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
		for (Pair pair: pairs){
			sm.setPairSubscription(pair, Constants.SubscriptionStatuses.Tradable);
		}
	}
	
	public void unsubscribeAll(){
		sm.removeAllPairSubscriptions();
	}
	
	public void recalibrateAllOrders(){
		for (Pair pair: pairs){
			try{
				sm.adjustOpposingOCOEntryOrders(pair, params.get(pair)[1]);
			} catch(NullPointerException e){
				Logger.error("Unable to recalibrate for " + pair + ", can't find OCO orders related to it");
			}
		}
	}
	
	private void setParam(Pair pair, int index, int value){
		params.get(pair)[index] = value;
	}
	
	
	public void setParams(Pair pair, int lots, int spikeBuffer, int stopBuffer){
		if(isActive){
			logIsRunning();
			return;
		}
		Logger.info("Setting parameters for " + pair + ": lots=" + lots + "K, SpikeBuffer=" 
				+ spikeBuffer + "pips, StopBuffer=" + stopBuffer + "pips");
		params.put(pair, new Integer[]{lots*1000, spikeBuffer, stopBuffer});
	}
	
	public void setRecalibrationOptions(Pair pair, boolean active){
		recalibrateParams.put(pair, active);
	}
	
	public void start(){
		if (!isActive){
			isActive = true;
			placeAllOrders();
			startRecalibrator();
		}
		else{
			logIsRunning();
		}
	}
	
	public void stop(){
		cancelAllOrders();
		try {
			Thread.sleep(500);
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
			Thread.sleep(500);
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		cancelAllOrders();
		stopRecalibrator();
		expirationTimer.cancel();
		if(dataCollect){
			dataCollector.cancel();
			for (Pair pair: pairs){
				rateCollectors.get(pair).end();
			}
		}
		try{
			Thread.sleep(3000); // wait for cleanup to finish
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally{
			sm.close();
		}
	}


}
