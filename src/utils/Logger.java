package utils;

public class Logger {
	
	public static LogModes logModeSet = LogModes.DEBUG;
	
	
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
			System.out.println(logMode.name + " - " + msg);
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
