package com.peebeekay.fx.rates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.listeners.RequestFailedException;
import com.peebeekay.fx.session.SessionDependent;
import com.peebeekay.fx.session.SessionManager;
import com.peebeekay.fx.utils.ArrayUtils;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.RateUtils;

public class RateCollector implements SessionDependent{
	
	private SessionManager sm;
	private Pair pair;
	private int length;
	private int frequency;
	public static final int MAX_REQUEST_LENGTH = 300;
	
	private boolean isActive = false;
	private int updatesCounter = 0; // keep track of how many points have been collected
	private Timer timer;
	
	private double maxWindowRange;
	private Timer maxWindowRangeUpdater;

	private ConcurrentLinkedQueue<Double> buyRates;
	private ConcurrentLinkedQueue<Double> sellRates;
	//private ConcurrentLinkedQueue<Double> highRates;
	//private ConcurrentLinkedQueue<Double> lowRates;
	
	
	public RateCollector(SessionManager sm, Pair pair, int length, int frequency) throws IllegalArgumentException{
			
//			if (length > 300){
//				throw new IllegalArgumentException("max allowed length of RateCollector is 300");
//			}
		
			if (length%30 != 0){
				throw new IllegalArgumentException("length must be a multiple of 30");
			}
			
			this.sm = sm;
			this.pair = pair;
			this.length = length;
			this.frequency = frequency;
			
			sm.registerDependent(this);
			
			buyRates = new ConcurrentLinkedQueue<Double>();
			sellRates = new ConcurrentLinkedQueue<Double>();
			//highRates = new ConcurrentLinkedQueue<Double>();
			//lowRates = new ConcurrentLinkedQueue<Double>();
			
			initialFill();
			
//			for (int i=0;i<length;i++){ //initialize with values
//				buyRates.add(sm.offersTable.getBuyRate(pair));
//				sellRates.add(sm.offersTable.getSellRate(pair));
//				highRates.add(sm.offersTable.getHigh(pair));
//				lowRates.add(sm.offersTable.getLow(pair));
//			}
			
			timer = new Timer();
			timer.schedule(new Update(), 0, frequency*1000);
			
			maxWindowRangeUpdater = new Timer();
			maxWindowRangeUpdater.schedule(new WindowRangeUpdater(), 0, 5000);
			
			isActive = true;

		}
	
	
	
	private void updateRates(){
		//System.out.println(pair + " at buy rate " + sm.offersTable.getBuyRate(pair));
		buyRates.add(sm.offersTable.getBuyRate(pair));
		sellRates.add(sm.offersTable.getSellRate(pair));
		//highRates.add(sm.offersTable.getHigh(pair));
		//lowRates.add(sm.offersTable.getLow(pair));
		
		buyRates.remove();
		sellRates.remove();
		//highRates.remove();
		//lowRates.remove();
		
		updatesCounter++;
		
	}
	
	private void initialFill(){
		
		if(length <= MAX_REQUEST_LENGTH){
			try {
				buyRates.addAll(RateHistory.getTickData(sm, pair, length, "buy"));
				sellRates.addAll(RateHistory.getTickData(sm, pair, length, "sell"));
				//highRates.addAll(RateHistory.getTickData(sm, pair, length, "high"));
				//lowRates.addAll(RateHistory.getTickData(sm, pair, length, "low"));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				buyRates.addAll(Collections.nCopies(length, sm.offersTable.getBuyRate(pair)));
				sellRates.addAll(Collections.nCopies(length, sm.offersTable.getSellRate(pair)));
			} catch (RequestFailedException e) {
			}
		}
		else{
			final int iterations = (int)(length/MAX_REQUEST_LENGTH);
			int iteration = 1;
			Calendar startPart = DateUtils.getUTCTime();
			Calendar endPart = DateUtils.getUTCTime(); endPart.add(Calendar.SECOND, -(length + 1));
			
			while(iteration <= iterations){
				startPart.setTime(endPart.getTime()); startPart.add(Calendar.SECOND, 1);
				endPart.setTime(startPart.getTime()); endPart.add(Calendar.SECOND, MAX_REQUEST_LENGTH - 1);
				
				ArrayList<ArrayList<Double>> rates = null;
				try {
					try {
						rates = RateHistory.getSnapshot(sm, pair, "t1", startPart, endPart);
					} catch (RequestFailedException e) {
					}
					int ratesSize = rates.get(0).size();
					if(ratesSize < MAX_REQUEST_LENGTH){ // if requesting rates for time when trading is closed, you will not retrieve the number of rates requested
						rates.get(0).addAll(Collections.nCopies(MAX_REQUEST_LENGTH - ratesSize, rates.get(0).get(ratesSize-1)));
						rates.get(1).addAll(Collections.nCopies(MAX_REQUEST_LENGTH - ratesSize, rates.get(1).get(ratesSize-1)));
					}
					
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				finally{
					buyRates.addAll(rates.get(0));
					sellRates.addAll(rates.get(1));
					iteration++;
				}
			}
			
			startPart.setTime(endPart.getTime()); startPart.add(Calendar.SECOND, 1);
			ArrayList<ArrayList<Double>> extras = null;
			try {
				try {
					extras = RateHistory.getSnapshot(sm, pair, "t1", startPart, DateUtils.getUTCTime());
				} catch (RequestFailedException e) {
				}
				buyRates.addAll(extras.get(0));
				sellRates.addAll(extras.get(1));
				for (int i=0; i < extras.get(0).size(); i++){ // to keep the size equal to length
					buyRates.remove();
					sellRates.remove();
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		updatesCounter += length;
		Logger.debug("rate collector for " + pair + " initialized with " + buyRates.size() + " ticks");
	}
	
	private class WindowRangeUpdater extends TimerTask{
		@Override
		public void run() {
			while(isActive){
				maxWindowRange = RateUtils.convertToPips(getMaxRangeByWindow(300), pair);
			}
		}
		
	}
	
	private double[] getQueue(String type){
		ConcurrentLinkedQueue<Double> q = null;
		switch(type){
		case "buy":
			q = buyRates;
			break;
		case "sell":
			q = sellRates;
			break;
//		case "high":
//			q = highRates;
//			break;
//		case "low":
//			q = lowRates;
//			break;
		default:
			return null;
		}
		Double[] source = q.toArray(new Double[length]);
		double[] res = new double[length];
		for(int i=0;i<length;i++){
			res[i] = source[i];
		}
		return res;
	}
	
	public void printRates(String type, int lastN_points){
		for (double d: getRates(type, lastN_points)){
			System.out.print(d + " ");
		}
	}
	
	public double[] getRates(String type, int lastN_points){
		if(lastN_points == 0){
			lastN_points = length;
		}
		double[] rates = getQueue(type);
		int first = length - lastN_points;
		return Arrays.copyOfRange(rates, first, length);
	}

	public double getSlope(String type, int lastN_points){
		if(lastN_points == 0){
			lastN_points = length;
		}
		double[] rates = getQueue(type);
		int lastPoint = length - 1;
		int basePoint = lastPoint - lastN_points;
		return (rates[lastPoint] - rates[basePoint])/lastN_points;
	}
	
	public double getHigh(String type, int lastN_points){
		if(lastN_points == 0){
			lastN_points = length;
		}
		double[] rates = getQueue(type);
		int first = rates.length - lastN_points;
		return ArrayUtils.max(Arrays.copyOfRange(rates, first, length));
	}
	
	public double getLow(String type, int lastN_points){
		if(lastN_points == 0){
			lastN_points = length;
		}
		double[] rates = getQueue(type);
		int first = rates.length - lastN_points;
		return ArrayUtils.min(Arrays.copyOfRange(rates, first, length));
	}
	
	public double getStdDev(String type){
		return (new DescriptiveStatistics(getQueue(type))).getStandardDeviation();
	}
	
	
	public Pair getPair(){
		return this.pair;
	}
	
	public int getLength(){
		return this.length;
	}
	
	public int getFrequency(){
		return this.frequency;
	}
	
	public double getMaxWindowRange(){
		return this.maxWindowRange;
	}
	
	class Update extends TimerTask{
		public void run(){
			updateRates();
		}
		
	}
	
	private double getMaxRangeByWindow(int windowLength){
		double rangeBuy = ArrayUtils.getMaxRangeByWindow(buyRates.toArray(new Double[length]), windowLength);
		double rangeSell = ArrayUtils.getMaxRangeByWindow(sellRates.toArray(new Double[length]), windowLength);
		if(rangeBuy > rangeSell){
			return rangeBuy;
		}
		return rangeSell;
	}
	
	
	public boolean isFull(){ //this rate collector has collected at least as much data as intended by length
		return updatesCounter >= length;
	}
	
	@Override
	public void end(){
		Logger.debug("cancelling rate collector for " + pair);
		timer.cancel();
		isActive = false;
		maxWindowRangeUpdater.cancel();
	}
	
	
	
	
	}
	
