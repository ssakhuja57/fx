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
	
	private String login;
	private String passwd;
	private String accountID;
	
	private String currency;
	private Date newsTime;
	
	private SessionManager sm;
	private ArrayList<String> pairs;
	private HashMap<String, RateCollector> rateCollectors;
	
	
	public void initialize(String currency, String newsTime_string){
		this.currency = currency;
		try {
			this.newsTime = (new SimpleDateFormat("YYYY-MM-dd hh:mm", Locale.ENGLISH)).parse(newsTime_string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pairs = Pairs.getRelatedPairs(currency);
		for (String pair:pairs){
			rateCollectors.put(pair, new RateCollector(sm, pair));
		}
	}

	@Override
	public void setSession(SessionManager sm) {
		this.sm = sm;
		
	}
}
