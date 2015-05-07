package com.peebeekay.fx.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.peebeekay.fx.listeners.RequestFailedException;
import com.peebeekay.fx.rates.RateHistory;
import com.peebeekay.fx.session.Credentials;
import com.peebeekay.fx.session.SessionManager;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.FXUtils;
import com.peebeekay.fx.utils.Logger;

public class DataCollector implements Runnable{

	private Credentials creds;
	private String pair;
	private String interval;
	private Date startDate;
	private Date endDate;
	private int sessionLimit;
	private File output;
	
	private HashMap<Integer,Collector> collectors = new HashMap<Integer,Collector>();
	private HashMap<Integer,Thread> collectorThreads = new HashMap<Integer,Thread>();
	private final int MAX_REQUEST_LIMIT = 300;
	
	public DataCollector(Credentials creds, String pair, String interval,
			Date startDate, Date endDate, int sessionLimit, String outputFilePath){
		this.creds = creds;
		this.pair = pair;
		this.interval = interval;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sessionLimit = sessionLimit;
		this.output = new File(outputFilePath);
	}
	
	@Override
	public void run(){

		Logger.info("Creating collectors");
		Logger.info("start: " + DateUtils.dateToString(startDate) + " end: " + DateUtils.dateToString(endDate));
		int seconds = DateUtils.secondsDiff(startDate, endDate);
		int step = seconds/sessionLimit;
		
		Calendar calendarItr = Calendar.getInstance();
		calendarItr.setTime(startDate);
		for(int i=0; i<sessionLimit; i++)
		{
			Date collectorStart = calendarItr.getTime();
			calendarItr.add(Calendar.SECOND, step);
			Collector c = null;
			try {
				c = new Collector(i, collectorStart, calendarItr.getTime());
			} catch (IOException e) {
				e.printStackTrace();
			}
			collectors.put(i, c);
			Thread cThread = new Thread(c);
			collectorThreads.put(i, cThread);
			cThread.start();
		}
		
		for(Thread t: collectorThreads.values()){
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class Collector implements Runnable{
		
		int id;
		SessionManager sm;
		Date start;
		Date end;
		
		String fileName;
		File f;
		FileWriter fw;
		BufferedWriter bfw;

		Collector(int id, Date start, Date end) throws IOException{
			Logger.info("creating collector " + id);
			this.id = id;
			fileName = output + "_" + id;
			f = new File(fileName);
			try {
				this.sm = new SessionManager(creds, null);
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.start = start;
			this.end = end;
			
			try{
				if(f.exists()){
					Logger.error("file " + fileName + " already exists");
					throw new IOException();
				}
				fw = new FileWriter(f);
				bfw = new BufferedWriter(fw);
			}
			catch(IOException e){
				Logger.error("error in trying to create file");
				e.printStackTrace();
				throw e;
			}
		}
		
		
		void writeData(Calendar startTime, Calendar endTime) throws IllegalArgumentException, IllegalAccessException, IOException{
			LinkedHashMap<Calendar, double[]> data;
			try {
				data = RateHistory.getSnapshotMapGreedy(sm, pair, interval, startTime, endTime);
				for(Calendar time: data.keySet()){
					double[] values = data.get(time);
					bfw.write(DateUtils.dateToString(time.getTime()) + "," + values[0] + "," + values[1] + "\n");
				}
			} catch (RequestFailedException e) {
			}
		}
		@Override
		public void run() {
			
			try{
				Calendar startChunk = Calendar.getInstance();
				Calendar endChunk = Calendar.getInstance();
				startChunk.setTime(start);
				endChunk.setTime(start);
				endChunk.add(Calendar.SECOND, MAX_REQUEST_LIMIT - 1);
				//Logger.info("Start chunk " + DateUtils.dateToString(startChunk.getTime()) + " End " + DateUtils.dateToString(end));
				while(startChunk.getTime().before(end)){
					try{
						if(FXUtils.checkMarketOpen(startChunk.getTime())){
							writeData(startChunk, endChunk);
						}
					} catch (IllegalArgumentException | IllegalAccessException
							| IOException e) {
						//e.printStackTrace();
					}
					finally{
						startChunk.setTime(endChunk.getTime());
						startChunk.add(Calendar.SECOND, 1);
						endChunk.setTime(startChunk.getTime());
						endChunk.add(Calendar.SECOND, MAX_REQUEST_LIMIT - 1);
					}
				}
			}
			finally{
				Logger.info("collector " + id + " completed");
				sm.close();
				try {
					bfw.close();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		
		// modify these
		String pair = "EUR/CAD";
		String parentFolder = "C:\\temp\\fx\\";
		String folder = parentFolder + "\\eur-cad\\";
		new File(folder).mkdirs();
		int accounts = 12;
		int sessionLimit = 25;
		
		Date[] starts = new Date[]{
				DateUtils.parseDate("01-01-2014 00:00:00"),
				DateUtils.parseDate("02-01-2014 00:00:00"),
				DateUtils.parseDate("03-01-2014 00:00:00"),
				DateUtils.parseDate("04-01-2014 00:00:00"),
				DateUtils.parseDate("05-01-2014 00:00:00"),
				DateUtils.parseDate("06-01-2014 00:00:00"),
				DateUtils.parseDate("07-01-2014 00:00:00"),
				DateUtils.parseDate("08-01-2014 00:00:00"),
				DateUtils.parseDate("09-01-2014 00:00:00"),
				DateUtils.parseDate("10-01-2014 00:00:00"),
				DateUtils.parseDate("11-01-2014 00:00:00"),
				DateUtils.parseDate("12-01-2014 00:00:00")
		};
		Date[] ends = new Date[]{
				DateUtils.parseDate("02-01-2014 00:00:00"),
				DateUtils.parseDate("03-01-2014 00:00:00"),
				DateUtils.parseDate("04-01-2014 00:00:00"),
				DateUtils.parseDate("05-01-2014 00:00:00"),
				DateUtils.parseDate("06-01-2014 00:00:00"),
				DateUtils.parseDate("07-01-2014 00:00:00"),
				DateUtils.parseDate("08-01-2014 00:00:00"),
				DateUtils.parseDate("09-01-2014 00:00:00"),
				DateUtils.parseDate("10-01-2014 00:00:00"),
				DateUtils.parseDate("11-01-2014 00:00:00"),
				DateUtils.parseDate("12-01-2014 00:00:00"),
				DateUtils.parseDate("01-01-2015 00:00:00")
		};
				
		
		for(int i=0; i<accounts; i++){
			Credentials creds = FXUtils.createDemoAccount();
			new DataCollector(creds, pair, "t1", starts[i], ends[i], sessionLimit, folder + i + ".csv").run();
		}
	}

}
