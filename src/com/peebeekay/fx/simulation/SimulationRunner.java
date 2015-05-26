package com.peebeekay.fx.simulation;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.distributors.ADataDistributor;
import com.peebeekay.fx.simulation.data.distributors.TickDataDistributor;
import com.peebeekay.fx.simulation.data.sources.DBDataSource;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.data.sources.RandomPriceData;
import com.peebeekay.fx.simulation.trader.ATrader;
import com.peebeekay.fx.simulation.trader.SimpleRSITrader;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.config.DBConfig;
import com.peebeekay.fx.utils.config.VerticaConfig;

public class SimulationRunner {
	
	
	
	
	public static void main(String[] args) throws ParseException{
		
		Pair pair = Pair.EURUSD;
		Calendar[] starts = new Calendar[]{
				DateUtils.getCalendar("2014-01-05 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-02-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-03-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-04-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-05-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-06-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-07-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-08-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-09-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-10-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-11-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-12-01 00:00:00", DateUtils.DATE_FORMAT_STD)
		};
		Calendar[] ends = new Calendar[]{
				DateUtils.getCalendar("2014-02-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-03-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-04-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-05-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-06-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-07-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-08-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-09-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-10-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-11-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2014-12-01 00:00:00", DateUtils.DATE_FORMAT_STD),
				DateUtils.getCalendar("2015-01-01 00:00:00", DateUtils.DATE_FORMAT_STD)
		};
		for(int month=0; month<starts.length; month++){
			DBConfig dbConfig = new VerticaConfig("192.168.0.102", 5433, "fx", "dbadmin", "dbadmin");
			DBDataSource dbSource = new DBDataSource(dbConfig, pair, starts[month], ends[month], true);
			SimulationController controller = new SimulationController(pair, starts[month], ends[month], dbSource);
			
			for(int i=12; i<=25; i++){
				SimpleRSITrader t = new SimpleRSITrader("2014-" + (month+1) + "-m30RSI-stop" + i + ".csv", "C:\\fx-data\\results", pair, dbSource, starts[month], i);
				controller.addTrader(t);
			}
			
			new Thread(controller).run();
		}
		
	}

}
