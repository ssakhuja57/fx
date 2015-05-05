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
			Thread collectorThread = new Thread(c);
			collectorThread.start();
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
			this.sm = new SessionManager(creds, null);
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
			LinkedHashMap<Calendar,double[]> data = 
					RateHistory.getSnapshotMap(sm, pair, interval, startTime, endTime);
			for(Calendar time: data.keySet()){
				double[] values = data.get(time);
				bfw.write(DateUtils.dateToString(time.getTime()) + "," + values[0] + "," + values[1] + "\n");
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
				sm.close();
				try {
					fw.close();
					bfw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws ParseException, IOException
	{
		
		// modify these
		String pair = "EUR/USD";
		String parentFolder = "C:\\temp\\fx\\";
		String folder = parentFolder + "\\eur-usd\\";
		new File(folder).mkdirs();
		int sessionLimit = 8;
		
		Credentials creds1 = new Credentials("D172741206001", "1008", "Demo", new String[]{"2743608", "2743608"});
		Credentials creds2 = new Credentials("D172791453001", "6270", "Demo", new String[]{"2743608", "2743608"});
		Credentials creds3 = new Credentials("D172791454001", "7407", "Demo", new String[]{"2743608", "2743608"});
		Credentials creds4 = new Credentials("D172791472001", "3550", "Demo", new String[]{"2743608", "2743608"});
		Credentials creds5 = new Credentials("D172791475001", "5933", "Demo", new String[]{"2743608", "2743608"});
		Credentials creds6 = new Credentials("D172791478001", "9048", "Demo", new String[]{"2743608", "2743608"});
		
		Date start1 = DateUtils.parseDate("01-01-2014 00:00:00"); Date end1 = DateUtils.parseDate("03-01-2014 00:00:00");
		Date start2 = DateUtils.parseDate("03-01-2014 00:00:00"); Date end2 = DateUtils.parseDate("05-01-2014 00:00:00");
		Date start3 = DateUtils.parseDate("05-01-2014 00:00:00"); Date end3 = DateUtils.parseDate("07-01-2014 00:00:00");
		Date start4 = DateUtils.parseDate("07-01-2014 00:00:00"); Date end4 = DateUtils.parseDate("09-01-2014 00:00:00");
		Date start5 = DateUtils.parseDate("09-01-2014 00:00:00"); Date end5 = DateUtils.parseDate("11-01-2014 00:00:00");
		Date start6 = DateUtils.parseDate("11-01-2014 00:00:00"); Date end6 = DateUtils.parseDate("01-01-2015 00:00:00");
		
		
		new Thread(new DataCollector(creds1, pair, "t1", start1, end1, sessionLimit, folder + "1.csv")).start();
		new Thread(new DataCollector(creds2, pair, "t1", start2, end2, sessionLimit, folder + "2.csv")).start();
		new Thread(new DataCollector(creds3, pair, "t1", start3, end3, sessionLimit, folder + "3.csv")).start();
		new Thread(new DataCollector(creds4, pair, "t1", start4, end4, sessionLimit, folder + "4.csv")).start();
		new Thread(new DataCollector(creds5, pair, "t1", start5, end5, sessionLimit, folder + "5.csv")).start();
		new Thread(new DataCollector(creds6, pair, "t1", start6, end6, sessionLimit, folder + "6.csv")).start();
		
	}

}
