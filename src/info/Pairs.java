package info;

import java.util.ArrayList;
import java.util.HashMap;

public class Pairs {
	
	private static HashMap<String, Integer[]> attributes = new HashMap<String, Integer[]>();
	
	static
	{
		attributes.put("EUR/USD", new Integer[] {1, 1});
		attributes.put("USD/JPY", new Integer[] {2, 2});
		attributes.put("GBP/USD", new Integer[] {3, 1});
		attributes.put("USD/CHF", new Integer[] {4, 2});
		attributes.put("EUR/CHF", new Integer[] {5, 2});
		attributes.put("AUD/USD", new Integer[] {6, 1});
		attributes.put("USD/CAD", new Integer[] {7, 2});
		attributes.put("NZD/USD", new Integer[] {8, 1});
		attributes.put("EUR/GBP", new Integer[] {9, 1});
		attributes.put("EUR/JPY", new Integer[] {10, 2});
		attributes.put("GBP/JPY", new Integer[] {11, 2});
		attributes.put("CHF/JPY", new Integer[] {12, 1});
		attributes.put("GBP/CHF", new Integer[] {13, 2});
		attributes.put("EUR/AUD", new Integer[] {14, 1});
		attributes.put("EUR/CAD", new Integer[] {15, 2});
		attributes.put("AUD/CAD", new Integer[] {16, 2});
		attributes.put("AUD/JPY", new Integer[] {17, 2});
		attributes.put("CAD/JPY", new Integer[] {18, 1});
		attributes.put("NZD/JPY", new Integer[] {19, 1});
		attributes.put("GBP/CAD", new Integer[] {20, 1});
		attributes.put("GBP/NZD", new Integer[] {21, 2});
		attributes.put("GBP/AUD", new Integer[] {22, 1});
		attributes.put("AUD/NZD", new Integer[] {28, 2});
		//ids.put("USD/SEK", new Integer[] {30, 0});
		//ids.put("EUR/SEK", new Integer[] {32, 2});
		//ids.put("EUR/NOK", new Integer[] {36, 1});
		//ids.put("USD/NOK", new Integer[] {37, 0});
		//ids.put("USD/MXN", new Integer[] {38, 0});
		attributes.put("AUD/CHF", new Integer[] {39, 1});
		attributes.put("EUR/NZD", new Integer[] {40, 1});
		//ids.put("USD/ZAR", new Integer[] {47, 0});
		//ids.put("USD/HKD", new Integer[] {50, 0});
		//ids.put("ZAR/JPY", new Integer[] {71, 0});
		//ids.put("USD/TRY", new Integer[] {83, 0});
		//ids.put("EUR/TRY", new Integer[] {87, 0});
		attributes.put("NZD/CHF", new Integer[] {89, 1});
		attributes.put("CAD/CHF", new Integer[] {90, 1});
		attributes.put("NZD/CAD", new Integer[] {91, 2});
		//ids.put("EUR/HUF", new Integer[] {96, 0});
		//ids.put("USD/HUF", new Integer[] {97, 0});
		//ids.put("TRY/JPY", new Integer[] {98, 0});
		//ids.put("USD/CNH", new Integer[] {105, 0});

	}
	
	public static String getID(String pair){
		return attributes.get(pair)[0].toString();
	}
	
	public static int getAccount(String pair){
		return attributes.get(pair)[1];
	}
	
//	public static void main(String[] args){
//		HashMap<String,Integer[]> counts = new HashMap<String,Integer[]>();
//		counts.put("USD", new Integer[]{0,0});
//		counts.put("EUR", new Integer[]{0,0});
//		counts.put("GBP", new Integer[]{0,0});
//		counts.put("JPY", new Integer[]{0,0});
//		counts.put("NZD", new Integer[]{0,0});
//		counts.put("CHF", new Integer[]{0,0});
//		counts.put("CAD", new Integer[]{0,0});
//		counts.put("AUD", new Integer[]{0,0});
//		for (String pair:attributes.keySet()){
//			int account = attributes.get(pair)[1];
//			String base = pair.substring(0, 3);
//			String quote = pair.substring(4, 7);
//			counts.get(base)[account-1]++;
//			counts.get(quote)[account-1]++;
//		}
//		for (String currency:counts.keySet()){
//			System.out.println(currency + ": " + counts.get(currency)[0] + " - " + counts.get(currency)[1]);
//		}
//	}
}
