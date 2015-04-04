package utils;

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
	public static Calendar getCalendar(String dateString) throws ParseException{
		return getCalendar(parseDate(dateString));
	}
	
	public static Calendar getCalendar(Date date){
		Calendar res = Calendar.getInstance();
		res.setTime(date);
		return res;
	}
	
	public static Date parseDate(String dateString) throws ParseException{
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)).parse(dateString);
	}
	
	public static Calendar getUTCTime(){
		return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}

}
