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
	
	public static final DateFormat DATE_FORMAT_STD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	public static final DateFormat DATE_FORMAT_MILLI = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
	
	public static Calendar getCalendar(String dateString, DateFormat df) throws ParseException{
		return getCalendar(parseDate(dateString, df));
	}
	
	public static Calendar getCalendar(Date date){
		Calendar res = Calendar.getInstance();
		res.setTime(date);
		return res;
	}
	
	public static Date parseDate(String dateString, DateFormat df) throws ParseException{
		return df.parse(dateString);
	}
	
	public static Date parseDate(String dateString) throws ParseException{
		return parseDate(dateString, DATE_FORMAT_STD);
	}
	
	public static Calendar getUTCTime(){
		return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}
	
	public static String dateToString(Date date){
		return DATE_FORMAT_STD.format(date);
	}
	
	public static String dateToString(Date date, DateFormat df){
		return df.format(date);
	}
	
	public static String calToString(Calendar cal){
		return dateToString(cal.getTime());
	}
	
	public static String calToString(Calendar cal, DateFormat df){
		return dateToString(cal.getTime(), df);
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
