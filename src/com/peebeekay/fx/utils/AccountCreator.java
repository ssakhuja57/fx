package com.peebeekay.fx.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccountCreator {
	
	
	private final String USER_AGENT = "Mozilla/5.0";
	 public static void main(String[] args) throws Exception {
	
	     AccountCreator ac = new AccountCreator();
	     System.out.println("Testing 1 - Send Http GET request");
	     ac.createAccount();
	
	 }
	 
	  // HTTP GET request
    public void createAccount() throws Exception {

            String url = "https://secure4.fxcorporate.com/tr-demo/"
                    + "form/service/?format=jsonp&rb=fxcm"
                    + "&DB=PremiumDemo&demo.firstname=s&demo.lastname=s"
                    + "&demo.email_address=afsf@afsd.fasdfasfjjj&demo.phone=7779568456"
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
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

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
                    username = fieldSplit[1];
                if(fieldSplit[0].trim().equals( "\"password\""))
                    password = fieldSplit[1];
            }
    }

}
