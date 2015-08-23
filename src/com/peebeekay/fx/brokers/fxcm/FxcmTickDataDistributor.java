package com.peebeekay.fx.brokers.fxcm;

import com.fxcore2.IO2GTableListener;
import com.fxcore2.O2GOfferTableRow;
import com.fxcore2.O2GRow;
import com.fxcore2.O2GTableStatus;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTableUpdateType;
import com.peebeekay.fx.data.ADataDistributor;
import com.peebeekay.fx.info.Interval;
import com.peebeekay.fx.simulation.data.IDataSubscriber;
import com.peebeekay.fx.simulation.data.types.Tick;

public class FxcmTickDataDistributor extends ADataDistributor implements IO2GTableListener{

	
	public FxcmTickDataDistributor(FxcmSessionManager fx) {
		fx.getTable(O2GTableType.OFFERS).subscribeUpdate(O2GTableUpdateType.UPDATE, this);
	}
	

	@Override
	public void onChanged(String rowID, O2GRow row) {
		distributeData((O2GOfferTableRow)row);
	}
	
	@Override
	public void onAdded(String arg0, O2GRow arg1) {
	}

	@Override
	public void onDeleted(String arg0, O2GRow arg1) {
	}

	@Override
	public void onStatusChanged(O2GTableStatus arg0) {
	}


	private void distributeData(O2GOfferTableRow row) {
		Tick t = FxcmUtils.offerToTick(row);
		for(IDataSubscriber ds: super.subscribers){
			ds.accept(t);
		}
	}

}
