package com.peebeekay.fx.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import com.peebeekay.fx.brokers.fxcm.FxcmSessionManager;
import com.peebeekay.fx.brokers.fxcm.FxcmRateHistory;
import com.peebeekay.fx.session.Credentials;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.FXUtils;
import com.peebeekay.fx.utils.Logger;

public class CollectorWorker implements Runnable{

	private int id;
	private FxcmSessionManager sm;
	private Date start;
	private Date end;
	
	String fileName;
	File f;
	private FileWriter fw;
	private BufferedWriter bfw;
	private Credentials creds;
	private String pair;
	private String interval;
	private File output;
	private final int MAX_REQUEST_LIMIT = 300;

	CollectorWorker(Credentials creds, String pair, String interval, int id, Date start, Date end) 
			throws IOException{
		Logger.info("creating collector " + id);
		this.id = id;
		fileName = output + "_" + id;
		f = new File(fileName);
		this.sm = new FxcmSessionManager(creds, null);
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
	
	
	void writeData(Calendar startTime, Calendar endTime) 
				throws IllegalArgumentException, IllegalAccessException, IOException{
		
		LinkedHashMap<Calendar,double[]> data = 
				FxcmRateHistory.getSnapshotMap(sm, pair, interval, startTime, endTime);
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
				bfw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
