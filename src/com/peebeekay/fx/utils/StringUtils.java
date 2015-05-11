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

}
