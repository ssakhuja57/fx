package com.peebeekay.fx.simulation;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.sources.DBDataSource;
import com.peebeekay.fx.simulation.data.sources.DelimitedFileDataSource;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.trader.ATrader;
import com.peebeekay.fx.simulation.trader.SimpleRSITrader;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.config.DBConfig;
import com.peebeekay.fx.utils.config.VerticaConfig;

public class SimulationRunner {
	
	
	
	
	public static void main(String[] args) throws ParseException{
		
		Pair pair = Pair.EURUSD;
		String dataFolder = "C:\\fx-data\\final\\recent\\EUR-USD\\";
		int maxConcurrentTrades = 1;
		
		Calendar[] starts = new Calendar[]{DateUtils.getCalendar("2014-03-28 11:00:00", DateUtils.DATE_FORMAT_STD)};
		Calendar[] ends = new Calendar[]{DateUtils.getCalendar("2014-03-29 00:00:00", DateUtils.DATE_FORMAT_STD)};
//		Calendar[] starts = new Calendar[]{
//				DateUtils.getCalendar("2014-01-05 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-02-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-03-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-04-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-05-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-06-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-07-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-08-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-09-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-10-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-11-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-12-01 00:00:00", DateUtils.DATE_FORMAT_STD)
//		};
//		Calendar[] ends = new Calendar[]{
//				DateUtils.getCalendar("2014-02-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-03-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-04-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-05-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-06-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-07-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-08-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-09-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-10-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-11-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2014-12-01 00:00:00", DateUtils.DATE_FORMAT_STD),
//				DateUtils.getCalendar("2015-01-01 00:00:00", DateUtils.DATE_FORMAT_STD)
//		};

		for(int x=0; x<starts.length; x++){
			DBConfig dbConfig = new VerticaConfig("192.168.91.152", 5433, "fx", "dbadmin", "dbadmin");
			IDataSource dataSource = new DBDataSource(dbConfig, pair, starts[x], ends[x], true);
			Map<Interval,File> ohlcFiles = new HashMap<Interval,File>();
//			ohlcFiles.put(Interval.M30, new File(dataFolder + "EURUSD-M30-0.csv_0"));
//			IDataSource dataSource = new DelimitedFileDataSource(dataFolder + "EURUSD-T-0.csv_0", ohlcFiles, ",", pair, starts[x], ends[x], true);
			SimulationController controller = new SimulationController(pair, starts[x], ends[x], dataSource);
			for(int i=10; i<=10; i+=5){
				ATrader t = new SimpleRSITrader("2015-" + (x+1) + "-m30RSI-stop" + i + ".csv", "C:\\fx-data\\results", 
						pair, dataSource, starts[x], i, maxConcurrentTrades);
				controller.addTrader(t);
			}
			
			new Thread(controller).run();
		}
		
	}

}
