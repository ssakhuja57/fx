package com.peebeekay.fx.simulation;

import java.text.ParseException;
import java.util.Calendar;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.sources.DBDataSource;
import com.peebeekay.fx.simulation.data.sources.IDataSource;
import com.peebeekay.fx.simulation.trader.ATrader;
import com.peebeekay.fx.simulation.trader.SimpleRSITrader;
import com.peebeekay.fx.utils.DateUtils;
import com.peebeekay.fx.utils.config.DBConfig;
import com.peebeekay.fx.utils.config.PostgresConfig;

public class SimulationRunner {
	
	
	
	
	public static void main(String[] args) throws ParseException{
		
		Pair pair = Pair.EURUSD;
		int maxConcurrentTrades = 1;
		
		Calendar[] starts = new Calendar[]{DateUtils.getCalendar("2015-01-15 00:00:00", DateUtils.DATE_FORMAT_STD)};
		Calendar[] ends = new Calendar[]{DateUtils.getCalendar("2015-01-30 00:00:00", DateUtils.DATE_FORMAT_STD)};

		for(int x=0; x<starts.length; x++){
			DBConfig dbConfig = new PostgresConfig("localhost", 5432, "postgres", "postgres", "postgres");
			IDataSource dataSource = new DBDataSource(dbConfig, pair, starts[x], ends[x], true);
			SimulationController controller = new SimulationController(pair, starts[x], ends[x], dataSource);
			for(int i=10; i<=15; i+=1){
//				ATrader t = new SimpleRSITrader("months-7thru12-m30RSI-stop" + i + ".csv", "C:\\fx-data\\results", 
				ATrader t = new SimpleRSITrader("year2015-m30RSI-recent-extremum-stop" + i + ".csv", "C:\\fx\\simulation-results",
						pair, dataSource, starts[x], i, maxConcurrentTrades);
				controller.addTrader(t);
			}
			
			new Thread(controller).run();
		}
		
	}

}
