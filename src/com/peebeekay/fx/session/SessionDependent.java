package com.peebeekay.fx.session;

import com.peebeekay.fx.brokers.fxcm.FxcmSessionManager;

public interface SessionDependent {

	public void close();
	
	public void reconnect();
}
