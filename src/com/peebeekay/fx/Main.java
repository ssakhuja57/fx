package com.peebeekay.fx;
import com.fxcore2.Constants;
import com.peebeekay.fx.brokers.fxcm.FxcmSessionManager;
import com.peebeekay.fx.brokers.fxcm.FxcmTickDataDistributor;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.session.Credentials;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.types.OhlcPrice;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.utils.Logger;


public class Main {
	
	public static void main(String[] args) throws Exception{
		
		Credentials creds = new Credentials("D172901772001", "600", "Demo", new String[]{"2904130", ""});
		FxcmSessionManager fx = new FxcmSessionManager(creds, null);
//		for(Calendar cal: FxcmRateHistory.getTickData(fx, Pair.EURUSD, 
//				DateUtils.getCalendar("2015-08-10 00:00:00"), DateUtils.getCalendar("2015-08-10 00:05:30")).keySet()){
//			Logger.info(DateUtils.calToString(cal));
//		}
//		O2GOfferRow row = fx.offersTable.getRateRow(Pair.EURUSD);
//		Logger.info(row.getInstrument() + " " + row.getAsk() + " " + row.getBid());
		
		FxcmTickDataDistributor tdd = new FxcmTickDataDistributor(fx);
		
		IDataSubscriber d = new IDataSubscriber() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Boolean isReady() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void accept(Tick price) {
				Logger.info(price.toString());
			}
			
			@Override
			public void accept(OhlcPrice price) {
				// TODO Auto-generated method stub
				
			}
		};
		
//		tdd.addSubscriber(d);
		
		Thread.sleep(1000);

		
		//System.out.println(sm.orders.getTradeIDs("GBP/AUD", Constants.Buy));
		//System.out.println(sm.trades.getTradeIDs("GBP/AUD", Constants.Buy));
		//System.out.println(sm.closedTrades.getTradeIDs("GBP/AUD", Constants.Buy));
//		fx.tradesTable.printTable();
//		fx.closedTradesTable.printTable();
		//sm.offersTable.printTable();
		fx.ordersTable.printTable();
//		fx.accountsTable.printTable();
		//SpikeTraderUI ui = new SpikeTraderUI();
		Logger.info(fx.tradesTable.getTradeIDs(Pair.EURUSD, Constants.Sell));
		
		Thread.sleep(5000);
		try {
			//System.out.println(sm.createMarketOrder("EUR/AUD", Constants.Buy, 1000));
			//sm.createEntryStopOrder("GBP/AUD", Constants.Buy, 1000, 1.93613, 2, true);
			//PositionActions.createMarketOrder(sm, "02730876", "EUR/USD", Constants.Buy, 1000, new ResponseListener());
			//System.out.println(sm.closeTrade("EUR/AUD", Constants.Buy));
			//sm.cancelOrder("GBP/AUD", Constants.Buy);
			//sm.createOpposingOCOEntryOrdersWithStops("EUR/GBP", 1000, 15, 5, true);
			//sm.createOpposingOCOEntryOrdersWithStops("EUR/USD", 1000, 20, 5, true);
			//sm.adjustOpposingOCOEntryOrders("EUR/USD", 1);
			//PositionActions.removeAllPairSubscriptions(sm, new ResponseListener());
			//sm.cancelAllOCOOrders();
			//SpikeTrader st = new SpikeTrader(sm, "USD", "2015-01-01 10:00");
//			for (double d : RateHistory.getTickData(sm, "EUR/JPY", 10, "sell")){
//				System.out.println(d);
//			}
			//SpikeTraderUI ui = new SpikeTraderUI();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			//sm.close();
		}
		
		
	}
}
	
	
	
	
	

