package com.peebeekay.fx.trades;

import com.peebeekay.fx.simulation.trader.TradeCreationException;
import com.peebeekay.fx.trades.specs.CreateTradeSpec;
import com.peebeekay.fx.trades.specs.UpdateTradeSpec;

public interface ITradeActionProvider {


		public String createOrder(CreateTradeSpec spec) throws TradeCreationException;

		public void closeTrade(Trade trade);

		public void updateTrade(Order order, UpdateTradeSpec spec);

		public void cancelOrder(Order order);
		
		
		public void adjustTradeStop(Trade trade, int newStopSize);
		
		public void adjustOrderStop(Order order, int newStopSize);


}
