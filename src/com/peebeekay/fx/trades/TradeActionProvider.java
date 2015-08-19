package com.peebeekay.fx.trades;

import com.peebeekay.fx.trades.specs.CreateTradeSpec;
import com.peebeekay.fx.trades.specs.UpdateTradeSpec;

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
