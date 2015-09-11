package com.peebeekay.fx.trades;

import com.peebeekay.fx.trades.specs.CreateTradeSpec;
import com.peebeekay.fx.trades.specs.UpdateTradeSpec;

public interface ITradeActionProvider {

		// returns orderId
		public String createOrder(CreateTradeSpec spec) throws OrderCreationException;

		public void closeTrade(Trade trade);

		public void updateTrade(Order order, UpdateTradeSpec spec);

		public void cancelOrder(Order order);
		
		public void adjustStop(Order order, double newPrice);


}
