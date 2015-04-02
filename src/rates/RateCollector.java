package rates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import session.SessionDependent;
import session.SessionManager;

public class RateCollector implements SessionDependent{
	
	private SessionManager sm;
	private String pair;
	private int length;
	private int frequency;
	
	private int updatesCounter = 0; // keep track of how many points have been collected
	private Timer timer;

	private ConcurrentLinkedQueue<Double> buyRates;
	private ConcurrentLinkedQueue<Double> sellRates;
	private ConcurrentLinkedQueue<Double> highRates;
	private ConcurrentLinkedQueue<Double> lowRates;
	
	
	public RateCollector(SessionManager sm, String pair, int length, int frequency) throws IllegalArgumentException{
			
			if (length > 300){
				throw new IllegalArgumentException("max allowed length of RateCollector is 300");
			}
			
			this.sm = sm;
			this.pair = pair;
			this.length = length;
			this.frequency = frequency;
			
			sm.registerDependent(this);
			
			buyRates = new ConcurrentLinkedQueue<Double>();
			sellRates = new ConcurrentLinkedQueue<Double>();
			highRates = new ConcurrentLinkedQueue<Double>();
			lowRates = new ConcurrentLinkedQueue<Double>();
			
			buyRates.addAll(RateHistory.getTickData(sm, pair, length, "buy"));
			sellRates.addAll(RateHistory.getTickData(sm, pair, length, "sell"));
			highRates.addAll(RateHistory.getTickData(sm, pair, length, "high"));
			lowRates.addAll(RateHistory.getTickData(sm, pair, length, "low"));
			
//			for (int i=0;i<length;i++){ //initialize with values
//				buyRates.add(sm.offersTable.getBuyRate(pair));
//				sellRates.add(sm.offersTable.getSellRate(pair));
//				highRates.add(sm.offersTable.getHigh(pair));
//				lowRates.add(sm.offersTable.getLow(pair));
//			}
			
			timer = new Timer();
			timer.schedule(new Update(), 0, frequency*1000);

		}
	
	
	
	private void updateRates(){
		//System.out.println(pair + " at buy rate " + sm.offersTable.getBuyRate(pair));
		buyRates.add(sm.offersTable.getBuyRate(pair));
		sellRates.add(sm.offersTable.getSellRate(pair));
		highRates.add(sm.offersTable.getHigh(pair));
		lowRates.add(sm.offersTable.getLow(pair));
		
		buyRates.remove();
		sellRates.remove();
		highRates.remove();
		lowRates.remove();
		
		updatesCounter++;
		
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
		case "high":
			q = highRates;
			break;
		case "low":
			q = lowRates;
			break;
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
		return max(Arrays.copyOfRange(rates, first, length));
	}
	
	public double getLow(String type, int lastN_points){
		if(lastN_points == 0){
			lastN_points = length;
		}
		double[] rates = getQueue(type);
		int first = rates.length - lastN_points;
		return min(Arrays.copyOfRange(rates, first, length));
	}
	
	public double getStdDev(String type){
		return (new DescriptiveStatistics(getQueue(type))).getStandardDeviation();
	}
	
	public String getPair(){
		return this.pair;
	}
	
	public int getLength(){
		return this.length;
	}
	
	public int getFrequency(){
		return this.frequency;
	}
	
	class Update extends TimerTask{
		public void run(){
			updateRates();
		}
		
	}
	
	private double max(double[] arr){
		double max = 0.0;
		for (double d: arr){
			if (d > max){
				max = d;
			}
		}
		return max;
	}
	
	private double min(double[] arr){
		double min = 99999;
		for (double d: arr){
			if (d < min){
				min = d;
			}
		}
		return min;
	}
	
	public boolean isFull(){ //this rate collector has collected at least as much data as intended by length
		return updatesCounter >= length;
	}
	
	@Override
	public void end(){
		System.out.println("cancelling rate collector for " + pair);
		timer.cancel();
	}
	
	
	
	
	}
	
