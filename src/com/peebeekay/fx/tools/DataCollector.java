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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

		Logger.info("Creating collectors - start: " + DateUtils.dateToString(startDate) + " end: " + DateUtils.dateToString(endDate));
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
//			collectors.put(i, c);
//			Thread cThread = new Thread(c);
//			collectorThreads.put(i, cThread);
//			cThread.start();
			c.run();
		}
		/**
		for(Thread t: collectorThreads.values()){
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
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
			Logger.info("creating collector " + id + ": " + DateUtils.dateToString(start) + " to " + DateUtils.dateToString(end));
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
			if(this.sm == null)
				return;
			LinkedHashMap<Calendar, double[]> data;
			data = RateHistory.getTickData(sm, pair, startTime, endTime);
			for(Calendar time: data.keySet()){
				double[] values = data.get(time);
				fw.write(DateUtils.dateToString(time.getTime(), DateUtils.DATE_FORMAT_MILLI) + "," + values[0] + "," + values[1] + "\n\r");
				fw.flush();
			}
		}
		@Override
		public void run() {
			if(this.sm == null)
				return;
			
			try{
				writeData(DateUtils.getCalendar(start), DateUtils.getCalendar(end));
			} catch (IllegalArgumentException | IllegalAccessException
					| IOException e) {
				e.printStackTrace();
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
		String pair = "EUR/USD";
		String parentFolder = "C:\\fx-data\\";
		String folder = parentFolder + "\\EUR-USD\\";
		new File(folder).mkdirs();
		int accounts = 6;
		int sessionLimit = 10;
		
		Date[] starts = new Date[]{
				DateUtils.parseDate("2014-01-01 00:00:00"),
				DateUtils.parseDate("2014-02-01 00:00:00"),
				DateUtils.parseDate("2014-03-01 00:00:00"),
				DateUtils.parseDate("2014-04-01 00:00:00"),
				DateUtils.parseDate("2014-05-01 00:00:00"),
				DateUtils.parseDate("2014-06-01 00:00:00"),
				DateUtils.parseDate("2014-07-01 00:00:00"),
				DateUtils.parseDate("2014-08-01 00:00:00"),
				DateUtils.parseDate("2014-09-01 00:00:00"),
				DateUtils.parseDate("2014-10-01 00:00:00"),
				DateUtils.parseDate("2014-11-01 00:00:00"),
				DateUtils.parseDate("2014-12-01 00:00:00")
		};
		Date[] ends = new Date[]{
				DateUtils.parseDate("2014-02-01 00:00:00"),
				DateUtils.parseDate("2014-03-01 00:00:00"),
				DateUtils.parseDate("2014-04-01 00:00:00"),
				DateUtils.parseDate("2014-05-01 00:00:00"),
				DateUtils.parseDate("2014-06-01 00:00:00"),
				DateUtils.parseDate("2014-07-01 00:00:00"),
				DateUtils.parseDate("2014-08-01 00:00:00"),
				DateUtils.parseDate("2014-09-01 00:00:00"),
				DateUtils.parseDate("2014-10-01 00:00:00"),
				DateUtils.parseDate("2014-11-01 00:00:00"),
				DateUtils.parseDate("2014-12-01 00:00:00"),
				DateUtils.parseDate("2015-01-01 00:00:00")
		};
		
		Credentials[] creds = new Credentials[accounts];
		for(int i=0; i<accounts;i++){
			creds[i] = FXUtils.createDemoAccount();
		}
		
		
				
		List<Thread> threads = new LinkedList<Thread>();
		for(int i=0; i<accounts; i++){
			//Credentials creds = new Credentials("D172741206001", "1008", Credentials.DEMO, null);
			//Thread t = new Thread(new DataCollector(creds[i%accounts], pair, "t1", starts[i], ends[i], sessionLimit, folder + i + ".csv"));
			
			new DataCollector(creds[i%accounts], pair, "t1", starts[i], ends[i], sessionLimit, folder + i + ".csv").run();
//			threads.add(t);
//			t.start();
			//Logger.info(DateUtils.dateToString(starts[i]) + " to " + DateUtils.dateToString(ends[i]));
		}
		
//		for(Thread th: threads){
//			th.join();
//		}
		
//		Calendar start = DateUtils.getCalendar("2014-05-31 15:58:45", DateUtils.DATE_FORMAT_STD);
//		Calendar end = DateUtils.getCalendar("2014-05-30 15:55:46", DateUtils.DATE_FORMAT_STD);
//		//Credentials creds2 = FXUtils.createDemoAccount();
//		Credentials creds2 = new Credentials("D172741206001", "1008", Credentials.DEMO, null);
//		LinkedHashMap<Calendar, double[]> values = RateHistory.getTickData(new SessionManager(creds2, null), "EUR/USD",
//				start, end);
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("C:\\fx-data\\testing.csv")));
//		for (Entry<Calendar, double[]> entry: values.entrySet()){
//			Logger.info(DateUtils.dateToString(entry.getKey().getTime()) + "," + entry.getValue()[0] + "," + entry.getValue()[1]);
//			//bw.write(DateUtils.dateToString(entry.getKey().getTime()) + "," + entry.getValue()[0] + "," + entry.getValue()[1] + "\n");
//		}
//		bw.close();
//		
		
	}

}
