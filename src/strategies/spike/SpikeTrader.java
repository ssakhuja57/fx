package strategies.spike;

import info.Pairs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import rates.RateCollector;
import session.SessionHolder;
import session.SessionManager;

public class SpikeTrader implements SessionHolder{
	
	private String currency;
	private Date eventDate;
	
	private SessionManager sm;
	private ArrayList<String> pairs;
	private HashMap<String, RateCollector> rateCollectors;
	
	
	public SpikeTrader(SessionManager sm, String currency, String eventDate_string){
		this.sm = sm;
		this.currency = currency;
		try {
			this.eventDate = (new SimpleDateFormat("YYYY-MM-dd hh:mm", Locale.ENGLISH)).parse(eventDate_string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		rateCollectors = new HashMap<String, RateCollector>();
		pairs = Pairs.getRelatedPairs(currency);
		for (String pair:pairs){
			rateCollectors.put(pair, new RateCollector(sm, pair));
		}
	}
	
	public String getCurrency(){
		return currency;
	}
	
	public String getEventDate(){
		return new SimpleDateFormat("YYYY-MM-dd HH:mm").format(new Date());
	}
	
	@Override
	public void close(){
		sm.close();
	}


}
