package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	public static LogModes logModeSet = LogModes.DEBUG;
	private final static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
	public static void info(String msg){
		log(msg, LogModes.INFO);
	}
	
	public static void error(String msg){
		log(msg, LogModes.ERROR);
	}
	
	public static void debug(String msg){
		log(msg, LogModes.DEBUG);
	}
	
	private static void log(String msg, LogModes logMode){
		if(logMode.value <= logModeSet.value){
			System.out.println(timeFormat.format(new Date()) + " - " + logMode.name + " - " + msg);
		}
	}
	
	public enum LogModes { 
		INFO(0, "INFO"), ERROR(0, "ERROR"), DEBUG(1, "DEBUG");
		
		private String name;
		private int value;
		
		private LogModes(int value, String name){
			this.value = value;
			this.name = name;
		}
	}
}
