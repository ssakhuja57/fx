package rates;

public class RateTools {
	
	public static double addPips(double rate, int pips){
		if (rate > 30){ //check if JPY
			return rate + pips/100.0;
		}
		return rate + pips/10000.0;
	}
	
}
