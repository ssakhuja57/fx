package com.peebeekay.fx.utils;

import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.ReferenceLine;
import com.peebeekay.fx.simulation.data.types.Tick;

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
	
	public static int getAbsPipDistance(double amount1, double amount2){
		int pips = 0;
		if(amount1 > 30 || amount2 > 30){
			pips = (int)convertToPips(amount1 - amount2, Pair.USDJPY);
		}
		else{
			pips = (int)convertToPips(amount1 - amount2, Pair.EURUSD);
		}
		return Math.abs(pips);
	}
	
	public static boolean isEqualOrBetter(Tick price, Tick reference, boolean isLong, boolean isEnter){
		if( isAsk(isLong, isEnter) ){
			if( price.getAsk() <= reference.getAsk() )
				return true;
			return false;
		}
		else{
			if( price.getBid() >= reference.getBid() )
				return true;
			return false;
		}
	}
	
	public static boolean isEqualOrBetter(Tick price, ReferenceLine reference, boolean isLong, boolean isEnter){
		if( isAsk(isLong, isEnter) ){
			if(price.getAsk() <= reference.getValue())
				return true;
			return false;
		}
		else{
			if(price.getBid() >= reference.getValue())
				return true;
			return false;
		}
	}
	
	public static boolean crosses(Tick price0, Tick price1, ReferenceLine reference, boolean isLong, boolean isEnter){
		if(isEqualOrBetter(price0, reference, isLong, isEnter) && !isEqualOrBetter(price1, reference, isLong, isEnter)){
			return true;
		}
		return false;
	}
	
	public static boolean isAsk(boolean isLong, boolean isEnter){
		return isLong == isEnter;
	}
	
	public static void main(String[] args){
		Logger.info(isAsk(true, false) + "");
	}
	
}
