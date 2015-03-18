package rates;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import session.SessionManager;

public class RateCollector{
	
	private SessionManager sm;
	private String pair;
	private int length;
	private int frequency;
	
	private Timer timer;

	private ConcurrentLinkedQueue<Double> buyRates;
	private ConcurrentLinkedQueue<Double> sellRates;
	private ConcurrentLinkedQueue<Double> highRates;
	private ConcurrentLinkedQueue<Double> lowRates;
	
	
	public RateCollector(SessionManager sm, String pair){
		this(sm, pair, 20, 1);
		this.length = 20;
		this.frequency = 1;
	}
	
	public RateCollector(SessionManager sm, String pair, int length, int frequency){
		
			this.sm = sm;
			this.pair = pair;
			this.length = length;
			this.frequency = frequency;
			
			buyRates = new ConcurrentLinkedQueue<Double>();
			sellRates = new ConcurrentLinkedQueue<Double>();
			highRates = new ConcurrentLinkedQueue<Double>();
			lowRates = new ConcurrentLinkedQueue<Double>();
			for (int i=0;i<length;i++){
				buyRates.add(0.0);
				sellRates.add(0.0);
				highRates.add(0.0);
				lowRates.add(0.0);
			}
			
			timer = new Timer();
			timer.schedule(new Update(), 0, frequency);

		}
	
	
	private void updateRates(){
		buyRates.add(sm.offersTable.getBuyRate(pair));
		sellRates.add(sm.offersTable.getSellRate(pair));
		highRates.add(sm.offersTable.getHigh(pair));
		lowRates.add(sm.offersTable.getLow(pair));
		
		buyRates.remove();
		sellRates.remove();
		highRates.remove();
		lowRates.remove();
		
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
		int counter = 0;
		double[] res = new double[length];
		for(double d:q.toArray(new Double[length])){
			res[counter] = d;
			counter++;
		}
		return res;
	}

	public double getSlope(String type, int lastN_points){
		double[] rates = getQueue(type);
		int lastPoint = length - 1;
		int basePoint = lastPoint - lastN_points;
		return (rates[lastPoint] - rates[basePoint])/lastN_points;
	}
	
	public double getHigh(String type, int lastN_points){
		double[] rates = getQueue(type);
		int first = rates.length - lastN_points;
		int last = length - 1;
		return max(Arrays.copyOfRange(rates, first, last));
	}
	
	public double getLow(String type, int lastN_points){
		double[] rates = getQueue(type);
		int first = rates.length - lastN_points;
		int last = length - 1;
		return min(Arrays.copyOfRange(rates, first, last));
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
		double min = 0.0;
		for (double d: arr){
			if (d < min){
				min = d;
			}
		}
		return min;
	}
	
	
	
	
	}
	
