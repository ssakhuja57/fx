package com.peebeekay.fx.trade;

import com.peebeekay.fx.trade.specs.CreateTradeSpec;
import com.peebeekay.fx.trade.specs.UpdateTradeSpec;

public interface TradeActionProvider {

		// return an orderId
		// trade spec should have the trade details 
		// like how to open, close, and any stops, etc.

		public String createOrder(CreateTradeSpec spec);

		public void closeTrade(String tradeId);

		public void updateTrade(String tradeId, UpdateTradeSpec spec);

		public void cancelOrder(String orderId);

		public String getTradeId(String orderId);


}
