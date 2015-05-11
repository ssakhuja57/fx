package com.peebeekay.fx.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;

public class DataValidator {
	
	public enum ValidationType{
		ASCENDING(0);
		private int value;
		private ValidationType(int value){
			this.value = value;
		}
	}

	File dir;
	String filePattern;
	boolean fixedStep;
	String interval;
	int timeFieldIndex;
	String delimiter;
	boolean ignoreBlanks;
	
	public DataValidator(String fileDir, String filePattern, boolean fixedStep, String interval, 
			String delimiter, int timeFieldIndex, boolean ignoreBlanks){
		this.dir = new File(fileDir);
		this.filePattern = filePattern;
		this.fixedStep = fixedStep;
		this.interval = interval;
		this.timeFieldIndex = timeFieldIndex;
		this.delimiter = delimiter;
		this.ignoreBlanks = ignoreBlanks;
	}
	
	private int validateAscending(File file, String delimiter, int timeFieldIndex) throws ParseException, IOException{
		BufferedReader br = null;
	    boolean success = true;
	    int lineNum = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
		    Calendar t0 = null;
		    Calendar t1 = null;
		    boolean gap = false;
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
		    	   if(!t1.after(t0)){
		    		   success = false;
		    		   break;
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
		File[] files = dir.listFiles();
		int filesScanned = 0;
		for(File file: files){
			String shortName = file.getName();
			if(shortName.matches(filePattern)){
				filesScanned++;
				Logger.info("validating file " + filesScanned + ": " + shortName);
				switch(type){
				case ASCENDING:
					int result = validateAscending(file, delimiter, timeFieldIndex);
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
		new DataValidator("C:\\fx-data\\EUR-USD", ".*", false, "t1", ",", 0, true).validate(ValidationType.ASCENDING);
	}
	
	

}
	
