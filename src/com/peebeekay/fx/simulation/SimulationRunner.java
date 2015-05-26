package com.peebeekay.fx.simulation;

import java.text.ParseException;
import java.util.Calendar;

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
		Calendar start = DateUtils.getCalendar("2014-01-05 00:00:00", DateUtils.DATE_FORMAT_STD);
		Calendar end = DateUtils.getCalendar("2014-02-10 00:00:00", DateUtils.DATE_FORMAT_STD);
//		Calendar end = DateUtils.getCalendar("2015-01-01 00:00:00", DateUtils.DATE_FORMAT_STD);
		
		DBConfig dbConfig = new VerticaConfig("192.168.0.102", 5433, "fx", "dbadmin", "dbadmin");
		DBDataSource dbSource = new DBDataSource(dbConfig, pair, start, end, true);
		
		SimpleRSITrader t1 = new SimpleRSITrader("Shubham", "C:\\fx-data\\results", pair, dbSource, start);
		
		SimulationController controller = new SimulationController(pair, start, end, dbSource);
		controller.addTrader(t1);
		
		new Thread(controller).start();
		
	}

}
