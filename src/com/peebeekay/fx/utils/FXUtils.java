package com.peebeekay.fx.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;

import com.peebeekay.fx.session.Credentials;

public class FXUtils {

	@SuppressWarnings("deprecation")
	public static boolean checkMarketOpen(Date dateUTC){
		if(dateUTC.getDay() == 6) return false;
		else if(dateUTC.getDay() == 5 && dateUTC.getHours() > 21) return false;
		else if(dateUTC.getDay() == 0 && dateUTC.getHours() < 21) return false;
		return true;
	}
	
	public static Credentials createDemoAccount() throws Exception {
		
		Logger.info("creating demo account");
		
		final String USER_AGENT = "Mozilla/5.0";
		String email = StringUtils.randString(8) + "@" + StringUtils.randString(5) + ".com";
        String url = "https://secure4.fxcorporate.com/tr-demo/"
                + "form/service/?format=jsonp&rb=fxcm"
                + "&DB=PremiumDemo"
                + "&demo.firstname=s&demo.lastname=s"
                + "&demo.email_address=" + email 
                + "&demo.phone=7779568456"
                + "&demo.country=united_states"
                + "&coReg=fxcm3_demo100k_trading-station&callback=function_34092717552874717000";

        URL obj = new URL(url);
        String username ="";
        String password ="";
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();
        String jsonResp = response.substring(response.indexOf("{") +1, response.indexOf("}"));
        String [] fields = jsonResp.split(",");
        for(String field:fields)
        {
            String [] fieldSplit = field.split(":");
            if(fieldSplit[0].trim().equals("\"username\""))
                username = fieldSplit[1].replace("\"", "");
            if(fieldSplit[0].trim().equals( "\"password\""))
                password = fieldSplit[1].replace("\"", "");
        }
        
        Thread.currentThread().sleep(1000);
        
        return new Credentials(username, password, Credentials.DEMO, null);
}

}
