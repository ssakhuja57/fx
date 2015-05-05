package com.peebeekay.fx.utils;

import java.util.Date;

public class FXUtils {

	@SuppressWarnings("deprecation")
	public static boolean checkMarketOpen(Date dateUTC){
		if(dateUTC.getDay() == 6) return false;
		else if(dateUTC.getDay() == 5 && dateUTC.getHours() > 21) return false;
		else if(dateUTC.getDay() == 0 && dateUTC.getHours() < 21) return false;
		return true;
	}

}
