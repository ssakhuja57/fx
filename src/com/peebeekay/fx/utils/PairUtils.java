package com.peebeekay.fx.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.peebeekay.fx.info.Pair;

public class PairUtils {
	
	public static String[] splitPair(Pair pair){
		return new String[]{pair.name().substring(0, 3), pair.name().substring(3, 6)};
	}
	
	public static Pair slashedStringToPair(String s){
		return Pair.valueOf(s.replace("/", ""));
	}
	
	public static String insertSlash(Pair pair){
		String[] currs = splitPair(pair);
		return currs[0] + "/" + currs[1];
	}

	public static ArrayList<Pair> getRelatedPairs(String[] currencies){
		ArrayList<Pair> pairs = new ArrayList<Pair>();
		for (Pair pair: EnumSet.allOf(Pair.class)){
			String[] currs = splitPair(pair);
			String curr1 = currs[0];
			String curr2 = currs[1];
			for(String currency: currencies){
				if( (currency.equals(curr1) || currency.equals(curr2))
						&& !pairs.contains(pair)){
						pairs.add(pair);
				}
			}
		}
		return pairs;
	}
	
	public static Collection<Pair> getAllPairs(){
		return EnumSet.allOf(Pair.class);
	}
	
	public static List<String> getAllCurrencies(){
		List<String> currs = new ArrayList<String>();
		for(Pair pair: getAllPairs()){
			for(String curr: splitPair(pair)){
				if(!currs.contains(curr))
					currs.add(curr);
			}
		}
		Collections.sort(currs);
		return currs;
	}
}
