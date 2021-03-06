package com.peebeekay.fx.brokers.fxcm;

import com.fxcore2.Constants;
import com.fxcore2.O2GOfferTableRow;
import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GTradeTableRow;
import com.peebeekay.fx.info.Pair;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.trades.Order;
import com.peebeekay.fx.trades.Order.OrderType;
import com.peebeekay.fx.trades.Trade;
import com.peebeekay.fx.utils.PairUtils;

public class FxcmUtils {

	
	public static Tick offerToTick(O2GOfferTableRow row){
		return new Tick(PairUtils.slashedStringToPair(row.getInstrument()), row.getTime().getTime(),
				row.getAsk(), row.getBid());
	}
	
	
	public static Order getOrder(O2GOrderTableRow row){
		String orderId = row.getOrderID();
		Pair pair = Pair.valueOf(Integer.parseInt(row.getOfferID()));
		boolean isLong = row.getBuySell() == Constants.Buy ? true : false;
		int lots = row.getAmount()/1000;
		double stopPrice = row.getStop();
		
		String orderTypeStr = row.getType();
		OrderType orderType = null;
		if(orderTypeStr == "OM")
			orderType = OrderType.Market;
		if(orderTypeStr == "STE")
			orderType = OrderType.Stop;
		else
			orderType = OrderType.Unknown;
		
		return new Order(orderId, orderType, pair, isLong, lots, stopPrice);
	}
	
	public static Trade getTrade(O2GTradeTableRow row){
		String orderId = row.getOpenOrderReqID();
		String accountId = row.getAccountID();
		Pair pair = Pair.valueOf(Integer.parseInt(row.getOfferID()));
		boolean isLong = (row.getBuySell().equals(Constants.Buy)) ? true : false;
		int lots = row.getAmount()/1000;
		double stopPrice = row.getStop();
		return new Trade(orderId, accountId, pair, isLong, lots, stopPrice);
	}
}
