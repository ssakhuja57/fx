package com.peebeekay.fx.trades;

import com.peebeekay.fx.info.Pair;

public interface IAccountInfoProvider {

	public double getTotalAccountBalance();
	public double getAvailableAccountBalance();
	public int getLots(Pair p, double accountValue);
	
}
