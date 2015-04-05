import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;

import rates.RateHistory;
import session.SessionManager;
import utils.DateUtils;


public class Test {

	public static void main(String[] args) throws ParseException {
		SessionManager sm = new SessionManager(null, "D26728250001", "6303", "Demo", "722858", "722858");
		try{
//			LinkedHashMap<Calendar, double[]> ticks = null;
//			try {
//				ticks = RateHistory.getSnapshotMap(sm, "EUR/USD", "t1", "2015-03-01 00:00", "2015-04-01 00:00");
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			for (Calendar time: ticks.keySet()){
//				double[] rates = ticks.get(time);
//				System.out.println(time.getTime().toString() + ": " + rates[0] + "/" + rates[1]);
//			}
			Collection<Double> ticks2 = RateHistory.getSnapshot(sm, "EUR/AUD", "t1", DateUtils.getCalendar("2015-04-04 21:56:00"),
					DateUtils.getCalendar("2015-04-04 21:58:00")).get(0);
//			Collection<Double> ticks2 = RateHistory.getTickData(sm, "EUR/USD", 999999, "buy");
			for (Double d: ticks2){
				System.out.println(d);
			}

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sm.close();
		}
		

	}

}
