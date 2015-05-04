package com.peebeekay.fx.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	
	private static DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
	
	public static Calendar getCalendar(String dateString) throws ParseException{
		return getCalendar(parseDate(dateString));
	}
	
	public static Calendar getCalendar(Date date){
		Calendar res = Calendar.getInstance();
		res.setTime(date);
		return res;
	}
	
	public static Date parseDate(String dateString) throws ParseException{
		return df.parse(dateString);
	}
	
	public static Calendar getUTCTime(){
		return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}
	
	public static String dateToString(Date date){
		return df.format(date);
	}
	
	public static int secondsDiff(Calendar c1, Calendar c2){
		return (int) ((c2.getTimeInMillis()-c1.getTimeInMillis())/1000);
	}
	
	public static int secondsDiff(Date d1, Date d2){
		return (int)((d2.getTime() - d1.getTime())/1000);
	}
	public static Date secondsAdd(Date d1, int secs)
	{
		return new Date((d1.getTime()/1000) + secs);
	}

}
