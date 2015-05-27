package com.peebeekay.fx.utils;

import java.lang.reflect.Field;
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
	
	
	public static String getFieldSummary(Object o){
		String res = "";
		for (Field field : o.getClass().getDeclaredFields()) {
		    field.setAccessible(true);
		    String name = field.getName();
		    Object value = null;
			try {
				value = field.get(o);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		    res += name + "=" + (value != null ? value.toString() : "") + "; ";
		}
		return res;
	}
	
	public static String arrayToString(Object[] arr, String delimiter){
		String res = "";
		for(Object o: arr){
			res += (o != null ? o.toString() : "") + delimiter;
		}
		return res.substring(0, res.length()-1);
	}

}
