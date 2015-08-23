package com.peebeekay.fx.brokers.fxcm;

import com.fxcore2.O2GOfferTableRow;
import com.peebeekay.fx.simulation.data.types.Tick;
import com.peebeekay.fx.utils.PairUtils;

public class FxcmUtils {

	
	public static Tick offerToTick(O2GOfferTableRow row){
		return new Tick(PairUtils.slashedStringToPair(row.getInstrument()), row.getTime().getTime(),
				row.getAsk(), row.getBid());
	}
}
