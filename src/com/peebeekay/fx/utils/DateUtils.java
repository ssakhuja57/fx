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
	
	public static final String DATE_FORMAT_STD = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_MILLI = "yyyy-MM-dd HH:mm:ss.SSS";
	
	public static DateFormat getDateFormat(String format){
		return new SimpleDateFormat(format, Locale.ENGLISH);
	}
	
	public static Calendar getCalendar(String dateString, String df) throws ParseException{
		return getCalendar(parseDate(dateString, df));
	}
	
	public static Calendar getCalendar(Date date){
		Calendar res = Calendar.getInstance();
		res.setTime(date);
		return res;
	}
	
	public static Date parseDate(String dateString, String df) throws ParseException{
		return getDateFormat(df).parse(dateString);
	}
	
	public static Date parseDate(String dateString) throws ParseException{
		return parseDate(dateString, DATE_FORMAT_STD);
	}
	
	public static Calendar getUTCTime(){
		return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}
	
	public static String dateToString(Date date){
		return getDateFormat(DATE_FORMAT_STD).format(date);
	}
	
	public static String dateToString(Date date, String df){
		return getDateFormat(df).format(date);
	}
	
	public static String calToString(Calendar cal){
		return dateToString(cal.getTime());
	}
	
	public static String calToString(Calendar cal, String df){
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
	
	
	public static boolean isMultipleOf(Date date, int minutes){
		return date.getMinutes() % minutes == 0;
	}
	
	public static Calendar roundDownToMinute(Calendar cal){
		String time = cal.get(Calendar.YEAR) + "-" + ( 1 + cal.get(Calendar.MONTH) ) + "-" + cal.get(Calendar.DAY_OF_MONTH)
				+ " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":00";
		try {
			return getCalendar(time, DATE_FORMAT_STD);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
