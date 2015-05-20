package com.peebeekay.fx.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;

public class DataValidator {
	
	public enum ValidationType{
		ASCENDING(0), DESCENDING(1);
		private int value;
		private ValidationType(int value){
			this.value = value;
		}
	}

	File dir;
	String filePattern;
	boolean fixedStep;
	Interval interval;
	int timeFieldIndex;
	String delimiter;
	boolean ignoreBlanks;
	
	public DataValidator(String fileDir, String filePattern, String delimiter, Interval interval, int timeFieldIndex, boolean ignoreBlanks){
		this.dir = new File(fileDir);
		this.filePattern = filePattern;
		this.timeFieldIndex = timeFieldIndex;
		this.delimiter = delimiter;
		this.ignoreBlanks = ignoreBlanks;
		this.interval = interval;
	}
	
	private int validateSequential(boolean ascending, File file, int timeFieldIndex) throws ParseException, IOException{
		BufferedReader br = null;
	    boolean success = true;
	    boolean fixedStep = interval.minutes != 0;
	    int step = 60*(ascending ? interval.minutes : -interval.minutes);
	    int lineNum = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
		    Calendar t0 = null;
		    Calendar t1 = null;
		    boolean gapWarning = false;
		    while ((line = br.readLine()) != null) {
		    	lineNum++;
		    	//Logger.info(line);
		    	if(line.trim().equals("") && ignoreBlanks){
		    		continue;
		    	}
		    	Calendar t = DateUtils.getCalendar(line.split(delimiter)[timeFieldIndex], DateUtils.DATE_FORMAT_MILLI);
		    	if(t1 == null){ // check if initial row
		    		t1 = t;
		    	}
		    	else{
		    	   t0 = t1;
		    	   t1 = t;
		    	   if( (ascending && !t1.after(t0)) || (!ascending && !t1.before(t0)) ){
		    		   success = false;
		    		   break;
		    	   }
		    	   if(fixedStep){
			    	   if(DateUtils.secondsDiff(t0, t1) != step){
			    		   if(gapWarning){
			    			   success = false;
			    			   break;
			    		   }
			    		   gapWarning = true;
			    	   }
			    	   else{
			    		   gapWarning = false;
			    	   }
		    	   }
		       }
		    }
		} finally{
			br.close();
		}
		if(!success){
			return lineNum;
		}
		return 0;
	}
	
	public boolean validate(ValidationType type) throws ParseException, IOException{
		
		boolean success = true;
		int result;
		File[] files = dir.listFiles();
		int filesScanned = 0;
		for(File file: files){
			String shortName = file.getName();
			if(shortName.matches(filePattern)){
				filesScanned++;
				Logger.info("validating file " + filesScanned + ": " + shortName);
				switch(type){
				case ASCENDING:
					result = validateSequential(true, file, timeFieldIndex);
					if(result != 0){
						Logger.error("error on line " + result + " for file " + shortName);
						success = false;
					}
					break;
				case DESCENDING:
					result = validateSequential(false, file, timeFieldIndex);
					if(result != 0){
						Logger.error("error on line " + result + " for file " + shortName);
						success = false;
					}
					break;
				}
			}
		}
		if(!success){
			Logger.error("validation not successful");
			return false;
		}
		Logger.info("validation successful!");
		return true;
	}
	
	public static void main(String[] args) throws ParseException, IOException{
		new DataValidator("C:\\fx-data\\EUR-USD", ".*", ",", Interval.M30, 0, true).validate(ValidationType.ASCENDING);
	}
	
	

}
	
