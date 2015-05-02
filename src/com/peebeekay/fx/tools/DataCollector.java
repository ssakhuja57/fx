package com.peebeekay.fx.tools;

import java.util.Date;

import com.peebeekay.fx.session.Credentials;
import com.peebeekay.fx.session.SessionManager;

public class DataCollector {

	private Credentials creds;
	private String pair;
	private String interval;
	private Date startDate;
	private Date endDate;
	private int sessionLimit;
	
	public DataCollector(Credentials creds, String pair, String interval,
			Date startDate, Date endDate, int sessionLimit){
		this.creds = creds;
		this.pair = pair;
		this.interval = interval;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sessionLimit = sessionLimit;
	}
	
	public void begin(){
		for(int i=1; i<=sessionLimit; i++){
			// chunk up the dates
			Date chunkStart = null;
			Date chunkEnd = null;
			SessionManager sm = new SessionManager(creds, null);
			new Collector(i, sm, chunkStart, chunkEnd);
		}
	}
	
	private class Collector implements Runnable{
		
		private int id;
		SessionManager sm;
		Date start;
		Date end;
		
		Collector(int id, SessionManager sm, Date start, Date end){
			this.id = id;
			this.sm = sm;
			this.start = start;
			this.end = end;
		}
		
		@Override
		public void run() {
			try{
				// do its thang
			}
			finally{
				sm.close();
			}
		}
	}

}
