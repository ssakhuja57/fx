package com.peebeekay.fx.utils;

import java.util.Random;

public class StringUtils {

	public static String randString(int length){
		String randStr = "";
		char [] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		Random rng = new Random();
		for(int i =0; i<length; i++)
			randStr += chars[rng.nextInt(chars.length)];
		return randStr;
	}
	
	public static String arrayToString(double[] arr, String delimiter){
		String res = String.valueOf(arr[0]);
		for(int i=1; i<arr.length; i++){
			res += delimiter + arr[i];
		}
		return res;
	}
	
	public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
	    if( c != null && string != null ) {
	        try {
	            return Enum.valueOf(c, string.trim().toUpperCase());
	        } catch(IllegalArgumentException ex) {
	        }
	    }
	    return null;
	}

}
