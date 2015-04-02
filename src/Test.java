import java.util.Collection;

import rates.RateHistory;
import session.SessionManager;


public class Test {

	public static void main(String[] args) {
		SessionManager sm = new SessionManager(null, "D26728250001", "6303", "Demo", "722858", "722858");
		try{
//			LinkedHashMap<Calendar, double[]> ticks = null;
//			try {
//				ticks = RateHistory.getSnapshot(sm, "EUR/USD", "t1", "2015-03-01 00:00", "2015-04-01 00:00");
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			for (Calendar time: ticks.keySet()){
//				double[] rates = ticks.get(time);
//				System.out.println(time.getTime().toString() + ": " + rates[0] + "/" + rates[1]);
//			}
			Collection<Double> ticks2 = RateHistory.getTickData(sm, "EUR/USD", 300, "buy");
			for (Double d: ticks2){
				System.out.println(d);
			}

		} finally{
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sm.close();
		}
		

	}

}
