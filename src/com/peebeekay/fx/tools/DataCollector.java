package com.peebeekay.fx.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.peebeekay.fx.brokers.fxcm.FxcmSessionManager;
import com.peebeekay.fx.brokers.fxcm.FxcmRateHistory;
import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.session.Credentials;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.FXUtils;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.StringUtils;

public class DataCollector implements Runnable{

	private Credentials creds;
	private Pair pair;
	private Interval interval;
	private Date startDate;
	private Date endDate;
	private int sessionLimit;
	private File output;
	
	private HashMap<Integer,Collector> collectors = new HashMap<Integer,Collector>();
	private HashMap<Integer,Thread> collectorThreads = new HashMap<Integer,Thread>();
	private final int MAX_REQUEST_LIMIT = 300;
	
	public DataCollector(Credentials creds, Pair pair, Interval interval,
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
			collectors.put(i, c);
			// Thread cThread = new Thread(c);
			// collectorThreads.put(i, cThread);
			//cThread.start();
			c.run();
		}
		
		// for(Thread t: collectorThreads.values()){
		// 	try {
		// 		t.join();
		// 	} catch (InterruptedException e) {
		// 		// TODO Auto-generated catch block
		// 		e.printStackTrace();
		// 	}
		// }
		
	}
	
	private class Collector implements Runnable{
		
		int id;
		FxcmSessionManager sm;
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
			this.sm = new FxcmSessionManager(creds, 0);
			this.start = start;
			this.end = end;
			
			if(f.exists()){
				Logger.info("appending to file " + fileName);
			}
			fw = new FileWriter(f, true);
			bfw = new BufferedWriter(fw);
			
		}
		
		
		void writeData(Calendar startTime, Calendar endTime) throws IllegalArgumentException, IllegalAccessException, IOException, InterruptedException{
			if(this.sm == null)
				return;
			Map<Calendar, double[]> data;
			if(interval == Interval.T)
				data = FxcmRateHistory.getTickData(sm, pair, startTime, endTime);
			else
				data = FxcmRateHistory.getOHLCData(sm, pair, interval, startTime, endTime);
			Logger.debug("retrieved " + data.size() + " rows of data");
			for(Calendar time: data.keySet()){
				String meta;
				if(interval == Interval.T)
					meta = pair + "," + DateUtils.dateToString(time.getTime(), DateUtils.DATE_FORMAT_MILLI) + ",";
				else
					meta = pair + "," + interval.name() + "," + DateUtils.dateToString(time.getTime(), DateUtils.DATE_FORMAT_MILLI) + ",";
				String values = StringUtils.arrayToString(data.get(time), ",");
//				Logger.debug(meta + values);
				fw.write(meta + values + "\n");
				fw.flush();
			}
			data.clear();
		}
		@Override
		public void run() {
			if(this.sm == null)
				return;
			
			try{
				writeData(DateUtils.getCalendar(start), DateUtils.getCalendar(end));
			} catch (IllegalArgumentException | IllegalAccessException
					| IOException | InterruptedException e) {
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
		Pair pair = Pair.EURUSD;
		Interval interval = Interval.T;
		String parentFolder = "C:\\fx\\data\\tick";
		String folder = parentFolder + "\\" + pair + "\\";
		new File(folder).mkdirs();
		int accounts = 1;
		int sessionLimit = 1;
		
		Date[] starts = new Date[]{
//				DateUtils.parseDate("2014-01-01 00:00:00"),
//				DateUtils.parseDate("2014-02-01 00:00:00"),
//				DateUtils.parseDate("2014-03-01 00:00:00"),
//				DateUtils.parseDate("2014-04-01 00:00:00"),
//				DateUtils.parseDate("2014-05-01 00:00:00"),
//				DateUtils.parseDate("2014-06-01 00:00:00"),
//				DateUtils.parseDate("2014-07-01 00:00:00"),
//				DateUtils.parseDate("2014-08-01 00:00:00"),
//				DateUtils.parseDate("2014-09-01 00:00:00"),
//				DateUtils.parseDate("2014-10-01 00:00:00"),
//				DateUtils.parseDate("2014-11-01 00:00:00"),
//				DateUtils.parseDate("2014-12-01 00:00:00")
				
				DateUtils.parseDate("2014-01-01 00:00:00")
		};
		Date[] ends = new Date[]{
//				DateUtils.parseDate("2014-02-01 00:00:00"),
//				DateUtils.parseDate("2014-03-01 00:00:00"),
//				DateUtils.parseDate("2014-04-01 00:00:00"),
//				DateUtils.parseDate("2014-05-01 00:00:00"),
//				DateUtils.parseDate("2014-06-01 00:00:00"),
//				DateUtils.parseDate("2014-07-01 00:00:00"),
//				DateUtils.parseDate("2014-08-01 00:00:00"),
//				DateUtils.parseDate("2014-09-01 00:00:00"),
//				DateUtils.parseDate("2014-10-01 00:00:00"),
//				DateUtils.parseDate("2014-11-01 00:00:00"),
//				DateUtils.parseDate("2014-12-01 00:00:00"),
//				DateUtils.parseDate("2015-01-01 00:00:00")
				
				DateUtils.parseDate("2015-01-01 00:00:00")
		};
		
		Credentials[] creds = new Credentials[accounts];
		for(int i=0; i<accounts;i++){
			//creds[i] = FXUtils.createDemoAccount();
			creds[i] = new Credentials("D172929194001", "9819", "Demo", new String[] { "your account number", ""});
		}
		
		int days = 1;
		Calendar start = DateUtils.getCalendar("2015-04-08 20:00:00");
		Calendar end = DateUtils.getCalendar("2015-09-23 00:00:00");
		Calendar endPart = Calendar.getInstance();
		endPart.setTime(start.getTime());
		endPart.add(Calendar.DATE, days);
//		List<Thread> threads = new LinkedList<Thread>();
		while(endPart.before(end)){
//			Credentials creds = new Credentials("D172741206001", "1008", Credentials.DEMO, null);
			new DataCollector(creds[0], pair, interval, start.getTime(), endPart.getTime(), sessionLimit, folder 
					+ pair + "-" + interval + ".csv").run();
			start.setTime(endPart.getTime());
			endPart.add(Calendar.DATE, days);
			Thread.sleep(30*1000);
		}
					

//		for(Thread th: threads){
//			th.join();
//		}
		
//		Calendar start = DateUtils.getCalendar("2014-05-30 15:50:45", DateUtils.DATE_FORMAT_STD);
//		Calendar end = DateUtils.getCalendar("2014-05-31 15:55:46", DateUtils.DATE_FORMAT_STD);
//		//Credentials creds2 = FXUtils.createDemoAccount();
//		Credentials creds2 = new Credentials("D172741206001", "1008", Credentials.DEMO, null);
//		LinkedHashMap<Calendar, double[]> values = RateHistory.getTickData(new SessionManager(creds2, null), "EUR/USD",
//				start, end);
//		//BufferedWriter bw = new BufferedWriter(new FileWriter(new File("C:\\fx-data\\testing.csv")));
//		for (Entry<Calendar, double[]> entry: values.entrySet()){
//			Logger.info(DateUtils.dateToString(entry.getKey().getTime()) + "," + entry.getValue()[0] + "," + entry.getValue()[1]);
//			//bw.write(DateUtils.dateToString(entry.getKey().getTime()) + "," + entry.getValue()[0] + "," + entry.getValue()[1] + "\n");
//		}
//		//bw.close();
//		
		
	}

}
