package actions;
import session.SessionManager;
import info.Pairs;
import listeners.ResponseListener;

import com.fxcore2.Constants;
import com.fxcore2.O2GRequest;
import com.fxcore2.O2GRequestFactory;
import com.fxcore2.O2GRequestParamsEnum;
import com.fxcore2.O2GResponse;
import com.fxcore2.O2GValueMap;


public class PositionActions {
	
	
	   public static String createMarketOrder(SessionManager sessionMgr, String accountID, String pair, String buySell, 
			   					int amount, ResponseListener responseListener) throws InterruptedException { 
	       
	        O2GValueMap valuemap = getDefaultValMap(sessionMgr, accountID, pair, buySell, amount);
	        
	        valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.TrueMarketOpen);
	        
	        return createOrder(sessionMgr, valuemap, responseListener);
	    }
	   

			
	   public static String createEntryOrderWithStop(SessionManager sessionMgr, String accountID, String pair, String buySell, 
			   					int amount, double rate, double stopOffset, boolean trailStop, ResponseListener responseListener) 
			   							throws InterruptedException { 
		   
		   stopOffset = buySell.equals(Constants.Buy) ? -stopOffset : stopOffset; // make offset negative if buy 
		   
		   O2GValueMap valuemap = getDefaultValMap(sessionMgr, accountID, pair, buySell, amount);
	        
	        valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.StopEntry);
	        valuemap.setString(O2GRequestParamsEnum.PEG_TYPE_STOP, Constants.Peg.FromClose);
	        valuemap.setDouble(O2GRequestParamsEnum.PEG_OFFSET_STOP, stopOffset);
	        valuemap.setDouble(O2GRequestParamsEnum.RATE, rate);
	        if(trailStop){
	        	valuemap.setInt(O2GRequestParamsEnum.TRAIL_STEP_STOP, 1);
	        }
	        
	        return createOrder(sessionMgr, valuemap, responseListener);
	    }
	   
	   // this is an alternative to using TrueMarketClose
	   public static String closeTrade(SessionManager sessionMgr, String accountID, String tradeID, String pair, String buySell, 
			   int amount, ResponseListener responseListener) throws InterruptedException { 
		
		   String oppositeBuySell = buySell.equals(Constants.Buy) ? Constants.Sell : Constants.Buy; 
		   
		   O2GValueMap valuemap = getDefaultValMap(sessionMgr, accountID, pair, buySell, amount);
		   
		   valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.TrueMarketOpen);
		   valuemap.setString(O2GRequestParamsEnum.TRADE_ID, tradeID);
			valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.CreateOrder);
	        valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
	        valuemap.setString(O2GRequestParamsEnum.OFFER_ID, Pairs.getID(pair).toString());
	        valuemap.setString(O2GRequestParamsEnum.BUY_SELL, oppositeBuySell);
	        valuemap.setInt(O2GRequestParamsEnum.AMOUNT, amount);
		   System.out.println("Close requested for " + pair + ":" + buySell + " at amount " + amount + " on account " + accountID);
		
		   return createOrder(sessionMgr, valuemap, responseListener);
	   }
	   
	   // the below returns an error saying that NFA rules do not permit this action
//	   public static String closeTrade(SessionManager sessionMgr, String accountID, String tradeID, String pair, String buySell, 
//			   int amount, ResponseListener responseListener) throws InterruptedException { 
//
//		   O2GValueMap valuemap = getDefaultValMap(sessionMgr, accountID, pair, buySell, amount);
//		   
//		   valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.TrueMarketClose);
//		   valuemap.setString(O2GRequestParamsEnum.TRADE_ID, tradeID);
//		   
//		   System.out.println("Close requested for " + pair + ":" + buySell + " at amount " + amount + " on account " + accountID);
//
//		   return createOrder(sessionMgr, valuemap, responseListener);
//	   }
	   
	   
	   public static String cancelOrder(SessionManager sessionMgr, String accountID, String orderID,
			   ResponseListener responseListener) throws InterruptedException { 
	
		   	O2GValueMap valuemap = getEmptyValMap(sessionMgr);
		   
		   valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.DeleteOrder);
	       valuemap.setString(O2GRequestParamsEnum.ORDER_ID, orderID);
	       valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
	       
		   return createOrder(sessionMgr, valuemap, responseListener);
	   }
	   


	   
		private static O2GValueMap getDefaultValMap(SessionManager sessionMgr, String accountID, String pair, String buySell, int amount){
	        
			O2GRequestFactory reqFactory = sessionMgr.session.getRequestFactory();
		       if (reqFactory == null) {
		           return null;
		       }
			O2GValueMap valuemap = reqFactory.createValueMap();
			valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.CreateOrder);
	        valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
	        valuemap.setString(O2GRequestParamsEnum.OFFER_ID, Pairs.getID(pair).toString());
	        valuemap.setString(O2GRequestParamsEnum.BUY_SELL, buySell);
	        valuemap.setInt(O2GRequestParamsEnum.AMOUNT, amount);
	        return valuemap;
		}
		
		private static O2GValueMap getEmptyValMap(SessionManager sessionMgr){
	        
			O2GRequestFactory reqFactory = sessionMgr.session.getRequestFactory();
		       if (reqFactory == null) {
		           return null;
		       }
			O2GValueMap valuemap = reqFactory.createValueMap();
	        return valuemap;
		}
		
		private static String createOrder(SessionManager sessionMgr, O2GValueMap valuemap, ResponseListener responseListener){
			O2GRequestFactory reqFactory = sessionMgr.session.getRequestFactory();
		       if (reqFactory == null) {
		           return null;
		       }
		        O2GRequest request = reqFactory.createOrderRequest(valuemap);
		        if (request != null) {
		        	String requestID = request.getRequestId();
		            responseListener.addRequestID(requestID); // Store requestId
		            sessionMgr.session.sendRequest(request);
		            return requestID;
		        } else {
		            System.out.println(reqFactory.getLastError());
		            return null;
		        }
		}
	   
}
