package com.peebeekay.fx.trades.specs;

import java.util.Map;

public class UpdateTradeSpec extends TradeSpec{

	public UpdateTradeSpec(){
		super();
	}
	
	public UpdateTradeSpec(Map<TradeProperty, String> tradeProperties) {
		super(tradeProperties);
	}

	@Override
	public String toString() {
		return "Type:Update" + super.getPropsString();
	}

}
