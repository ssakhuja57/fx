package com.peebeekay.fx.trades;

import com.peebeekay.fx.info.Pair;

public interface IAccountInfoProvider {

	public double getTotalUsableAccountBalance();
	public double getAvailableUsableAccountBalance();
	public int getLots(Pair p, double accountValue);
	double getAvailableUsablePercentAccountBalance();
	double getPercentMaxAccountUse();
	
}
