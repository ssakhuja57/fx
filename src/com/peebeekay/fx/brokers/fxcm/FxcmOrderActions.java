package com.peebeekay.fx.brokers.fxcm;
import java.util.Collection;

import com.fxcore2.Constants;
import com.fxcore2.O2GAccountRow;
import com.fxcore2.O2GAccountsTableResponseReader;
import com.fxcore2.O2GLoginRules;
import com.fxcore2.O2GMargin;
import com.fxcore2.O2GRequest;
import com.fxcore2.O2GRequestFactory;
import com.fxcore2.O2GRequestParamsEnum;
import com.fxcore2.O2GResponse;
import com.fxcore2.O2GResponseReaderFactory;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTradingSettingsProvider;
import com.fxcore2.O2GValueMap;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.listeners.ResponseListener;
import com.peebeekay.fx.utils.Logger;
import com.peebeekay.fx.utils.PairUtils;


public class FxcmOrderActions {
	
	
	   public static String createMarketOrder(FxcmSessionManager sessionMgr, String accountID, Pair pair, String buySell, 
			   					int amount, ResponseListener responseListener) { 
	       
	        O2GValueMap valuemap = getDefaultValMap(sessionMgr, accountID, pair, buySell, amount);
	        
	        valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.TrueMarketOpen);
	        
	        Logger.info("creating market order for " + pair + " of amount " + amount/1000 + "K and type " + buySell);
	        
	        return createOrder(sessionMgr, valuemap, responseListener);
	    }
	   
	   public static String createMarketOrderWithStop(FxcmSessionManager sessionMgr, String accountID, Pair pair,
			   String buySell, int amount, int stopOffset, boolean trailStop, ResponseListener responseListener){
		   
		   stopOffset = buySell.equals(Constants.Buy) ? -stopOffset : stopOffset; // make offset negative if buy 
		   
		   O2GValueMap valuemap = getDefaultValMap(sessionMgr, accountID, pair, buySell, amount);
	        
	        valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.TrueMarketOpen);
	        valuemap.setString(O2GRequestParamsEnum.PEG_TYPE_STOP, Constants.Peg.FromClose);
	        valuemap.setInt(O2GRequestParamsEnum.PEG_OFFSET_STOP, stopOffset);
	        if(trailStop){
	        	valuemap.setInt(O2GRequestParamsEnum.TRAIL_STEP_STOP, 1);
	        }
	        Logger.info("Creating market order on " + pair + "of amount " + amount/1000 + "K of type " + buySell + " with stop offset of "
	        		+ stopOffset + (trailStop ? " with trail" : ""));
	        
	        return createOrder(sessionMgr, valuemap, responseListener);
	   }
	   

			
	   public static String createEntryOrderWithStop(FxcmSessionManager sessionMgr, String accountID, Pair pair, String buySell, 
			   					int amount, double rate, int stopOffset, boolean trailStop, ResponseListener responseListener){ 
		   
		   stopOffset = buySell.equals(Constants.Buy) ? -stopOffset : stopOffset; // make offset negative if buy 
		   
		   O2GValueMap valuemap = getDefaultValMap(sessionMgr, accountID, pair, buySell, amount);
	        
	        valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.StopEntry);
	        valuemap.setString(O2GRequestParamsEnum.PEG_TYPE_STOP, Constants.Peg.FromClose);
	        valuemap.setInt(O2GRequestParamsEnum.PEG_OFFSET_STOP, stopOffset);
	        valuemap.setDouble(O2GRequestParamsEnum.RATE, rate);
	        if(trailStop){
	        	valuemap.setInt(O2GRequestParamsEnum.TRAIL_STEP_STOP, 1);
	        }
	        Logger.info("Creating entry order on " + pair + " of amount " + amount/1000 + "K of type " + buySell + " with stop offset of "
	        		+ stopOffset + (trailStop ? " with trail" : ""));
	        
	        return createOrder(sessionMgr, valuemap, responseListener);
	    }
	   
	   public static void adjustStop(FxcmSessionManager sessionMgr, String accountID, String orderID, int newStopOffset, ResponseListener responseListener){
		   
		   O2GValueMap valuemap = getEmptyValMap(sessionMgr);
		   	valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.EditOrder);
		   	valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
		   	valuemap.setString(O2GRequestParamsEnum.ORDER_ID, orderID);
		   	valuemap.setString(O2GRequestParamsEnum.PEG_TYPE, Constants.Peg.FromClose);
		   	valuemap.setInt(O2GRequestParamsEnum.PEG_OFFSET, newStopOffset);
		   	
		   	createOrder(sessionMgr, valuemap, responseListener);
	   }
	   
	   public static void adjustStop(FxcmSessionManager sessionMgr, String accountID, String orderID, double newRate, ResponseListener responseListener){
		   
		   O2GValueMap valuemap = getEmptyValMap(sessionMgr);
		   	valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.EditOrder);
		   	valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
		   	valuemap.setString(O2GRequestParamsEnum.ORDER_ID, orderID);
		   	valuemap.setDouble(O2GRequestParamsEnum.RATE, newRate);
		   	valuemap.setInt(O2GRequestParamsEnum.TRAIL_STEP, 1);
		   	
		   	createOrder(sessionMgr, valuemap, responseListener);
	   }
	   
	   public static String createOpposingOCOEntryOrdersWithStops(FxcmSessionManager sessionMgr, String accountID, Pair pair,
			   int amount, double longRate, double shortRate, int stopOffset, boolean trailStop, ResponseListener responseListener){
		   O2GValueMap parentValueMap = getEmptyValMap(sessionMgr);
		   parentValueMap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.CreateOCO);
		   
		   O2GValueMap buyMap = getDefaultValMap(sessionMgr, accountID, pair, Constants.Buy, amount);
	        buyMap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.StopEntry);
		   //buyMap.setString(O2GRequestParamsEnum.ORDER_TYPE, "OR");
		   //buyMap.setDouble(O2GRequestParamsEnum.RATE_MIN, RateTools.addPips(longRate, -10));//
	       //buyMap.setDouble(O2GRequestParamsEnum.RATE_MAX, RateTools.addPips(longRate, 10));//
	        buyMap.setString(O2GRequestParamsEnum.PEG_TYPE_STOP, Constants.Peg.FromClose);
	        buyMap.setInt(O2GRequestParamsEnum.PEG_OFFSET_STOP, -stopOffset);
	        buyMap.setDouble(O2GRequestParamsEnum.RATE, longRate);
	        if(trailStop){
	        	buyMap.setInt(O2GRequestParamsEnum.TRAIL_STEP_STOP, 1);
	        }
	       O2GValueMap sellMap = getDefaultValMap(sessionMgr, accountID, pair, Constants.Sell, amount);
	        sellMap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.StopEntry);
	       //sellMap.setString(O2GRequestParamsEnum.ORDER_TYPE, "OR");
	       //sellMap.setDouble(O2GRequestParamsEnum.RATE_MIN, RateTools.addPips(shortRate, -10));//
	       //sellMap.setDouble(O2GRequestParamsEnum.RATE_MAX, RateTools.addPips(shortRate, 10));//
	        sellMap.setString(O2GRequestParamsEnum.PEG_TYPE_STOP, Constants.Peg.FromClose);
	        sellMap.setInt(O2GRequestParamsEnum.PEG_OFFSET_STOP, stopOffset);
	        sellMap.setDouble(O2GRequestParamsEnum.RATE, shortRate);
	        if(trailStop){
	        	sellMap.setInt(O2GRequestParamsEnum.TRAIL_STEP_STOP, 1);
	        }
	       
	        parentValueMap.appendChild(buyMap);
	        parentValueMap.appendChild(sellMap);
	        
	        Logger.info("OCO orders on " + pair + " of amount " + amount/1000 + "K with long at " + longRate + ", short at " + shortRate
	        		+ " and stop offset of " + stopOffset);
	        
	        return createOrder(sessionMgr, parentValueMap, responseListener);
	   }
	   
	   public static void adjustOpposingOCOEntryOrders(FxcmSessionManager sessionMgr, String accountID, String longOrderID, double newLongRate,
			   String shortOrderID, double newShortRate, ResponseListener responseListener){
		   
		   //System.out.println("adjusting OCO orders " + longOrderID + "/" + shortOrderID);
		   	
		   O2GValueMap valuemapLong = getEmptyValMap(sessionMgr);
		   	valuemapLong.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.EditOrder);
		   	valuemapLong.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
		   	valuemapLong.setString(O2GRequestParamsEnum.ORDER_ID, longOrderID);
		   	valuemapLong.setDouble(O2GRequestParamsEnum.RATE, newLongRate);
		   	
		   O2GValueMap valuemapShort = getEmptyValMap(sessionMgr);
		   	valuemapShort.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.EditOrder);
		   	valuemapShort.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
		   	valuemapShort.setString(O2GRequestParamsEnum.ORDER_ID, shortOrderID);
		   	valuemapShort.setDouble(O2GRequestParamsEnum.RATE, newShortRate);
		   	
		   	createOrder(sessionMgr, valuemapLong, responseListener);
		   	createOrder(sessionMgr, valuemapShort, responseListener);
	   }
	   
	   // this is an alternative to using TrueMarketClose
	   public static String closeTrade(FxcmSessionManager sessionMgr, String accountID, Pair pair, String buySell, 
			   int amount, ResponseListener responseListener){ 
		
		   String oppositeBuySell = buySell.equals(Constants.Buy) ? Constants.Sell : Constants.Buy; 

		   Logger.info("Close requested for " + pair + ":" + buySell + " at amount " + amount + " on account " + accountID);
		
	        return createMarketOrder(sessionMgr, accountID, pair, oppositeBuySell, amount, responseListener);
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
	   
	   
	   public static String cancelOrder(FxcmSessionManager sessionMgr, String accountID, String orderID,
			   ResponseListener responseListener) throws InterruptedException { 
	
		   	O2GValueMap valuemap = getEmptyValMap(sessionMgr);
		   
		   valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.DeleteOrder);
	       valuemap.setString(O2GRequestParamsEnum.ORDER_ID, orderID);
	       valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
	       
		   return createOrder(sessionMgr, valuemap, responseListener);
	   }
	   
	   public static void cancelAllOCOOrders(FxcmSessionManager sessionMgr, ResponseListener responseListener) throws InterruptedException{
		   Logger.info("cancelling all OCO orders");
		   for (String[] ids: sessionMgr.ordersTable.getAllOCOOrderIDs()){
			   cancelOrder(sessionMgr, ids[0], ids[1], responseListener);
		   }
	   }
	   
	   public static String setPairSubscription(FxcmSessionManager sessionMgr, Pair pair, String status, ResponseListener responseListener){
		   
		   if (status.equals(Constants.SubscriptionStatuses.Tradable)){
			   int subscribed = sessionMgr.offersTable.getSubscriptionCount();
			   int limit = sessionMgr.subscriptionLimit;
			   if (subscribed >= limit){
				   Logger.error("already subscribed to the set limit of " + limit);
				   Logger.error("you must unsubscribe pairs first...");
				   return null;
			   }
		   }
		   
		   Logger.info("Setting " + pair + " to " + status);
		   
		   O2GValueMap valuemap = getEmptyValMap(sessionMgr);
	        valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.SetSubscriptionStatus);
	        valuemap.setString(O2GRequestParamsEnum.SUBSCRIPTION_STATUS, status);
	        valuemap.setString(O2GRequestParamsEnum.OFFER_ID, String.valueOf(pair.id));
	        
	        return createOrder(sessionMgr, valuemap, responseListener);
	   }
	   
	   public static void removeAllPairSubscriptions(FxcmSessionManager sessionMgr, ResponseListener responseListener){
		   Logger.info("unsubscribing from all pairs...");
		   for (Pair pair: PairUtils.getAllPairs()){
			   setPairSubscription(sessionMgr, pair, Constants.SubscriptionStatuses.Disable, responseListener);
		   }
	   }
	   
	   public static void setPairSubscriptionByCurrency(FxcmSessionManager sessionMgr, String[] currencies, ResponseListener responseListener){
		   
		   Logger.info("Setting all pairs related to " 
				   + currencies.toString() + " to tradable");
		   Collection<Pair> pairs = PairUtils.getRelatedPairs(currencies);
		   for (Pair pair: pairs){
			   setPairSubscription(sessionMgr, pair, Constants.SubscriptionStatuses.Tradable, responseListener);
		   }
	   }
	   
	   public static void updateMarginRequirements(FxcmSessionManager sessionMgr, ResponseListener responseListener) {
		    O2GRequestFactory requestFactory = sessionMgr.session.getRequestFactory();
		    if (requestFactory != null) {
		    	O2GValueMap valuemap = getEmptyValMap(sessionMgr);
		        valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.UpdateMarginRequirements);
			    createOrder(sessionMgr, valuemap, responseListener);
		    }
		 }
		 
		public static double[] getMarginRequirements(FxcmSessionManager sessionMgr, Pair pair) {
		        O2GLoginRules loginRules = sessionMgr.session.getLoginRules();
		        O2GTradingSettingsProvider tradingSetting = loginRules.getTradingSettingsProvider();
		        O2GResponse accountsResponse = loginRules.getTableRefreshResponse(O2GTableType.ACCOUNTS);
		        O2GResponseReaderFactory responseReaderFactory = sessionMgr.session.getResponseReaderFactory();
		        if (responseReaderFactory == null) {
		            return null;
		        }
		        O2GAccountsTableResponseReader accounts = responseReaderFactory.createAccountsTableReader(accountsResponse);
		        O2GAccountRow accountRow = accounts.getRow(0);
		        O2GMargin margin = tradingSetting.getMargins(PairUtils.insertSlash(pair), accountRow);
		        return new double[]{margin.getMMR(), margin.getEMR(), margin.getLMR()};
		 }
	   


	   
		private static O2GValueMap getDefaultValMap(FxcmSessionManager sessionMgr, String accountID, Pair pair, String buySell, int amount){
	        
			O2GRequestFactory reqFactory = sessionMgr.session.getRequestFactory();
		       if (reqFactory == null) {
		           return null;
		       }
			O2GValueMap valuemap = reqFactory.createValueMap();
			valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.CreateOrder);
	        valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
	        valuemap.setString(O2GRequestParamsEnum.OFFER_ID, String.valueOf(pair.id));
	        valuemap.setString(O2GRequestParamsEnum.BUY_SELL, buySell);
	        valuemap.setInt(O2GRequestParamsEnum.AMOUNT, amount);
	        return valuemap;
		}
		
		private static O2GValueMap getEmptyValMap(FxcmSessionManager sessionMgr){
	        
			O2GRequestFactory reqFactory = sessionMgr.session.getRequestFactory();
		       if (reqFactory == null) {
		           return null;
		       }
			O2GValueMap valuemap = reqFactory.createValueMap();
	        return valuemap;
		}
		
		private static String createOrder(FxcmSessionManager sessionMgr, O2GValueMap valuemap, ResponseListener responseListener){
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
		            Logger.error(reqFactory.getLastError());
		            return null;
		        }
		}
	   
}
