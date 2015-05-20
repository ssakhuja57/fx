package com.peebeekay.fx.utils;

import com.peebeekay.fx.info.Pair;

public class RateUtils {
	
	public static double addPips(double rate, int pips){
		if (rate > 30){ //check if JPY
			return rate + pips/100.0;
		}
		return rate + pips/10000.0;
	}
	
	public static double convertToPips(double amount, Pair pair){
		String[] currencies = PairUtils.splitPair(pair);
		if (currencies[0].equals("JPY") || currencies[1].equals("JPY")){
			return Math.round(100*amount*10)/10;
		}
		return Math.round(10000*amount*10)/10;
	}
	
}
