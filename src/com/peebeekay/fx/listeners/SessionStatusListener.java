package com.peebeekay.fx.listeners;
import com.fxcore2.IO2GSessionStatus;
import com.fxcore2.O2GSessionStatusCode;
import com.peebeekay.fx.utils.Logger;


public class SessionStatusListener implements IO2GSessionStatus{
	
	String sessionStatus;
	private final int LOGIN_WAIT_TIME = 10;
	
	@Override
	public void onLoginFailed(String arg0) {
		Logger.error("Unable to login:");
		Logger.error(arg0);
	}

	@Override
	public void onSessionStatusChanged(O2GSessionStatusCode statusCode) {
		sessionStatus = statusCode.name().toString();
		Logger.info(statusCode.name().toString());
	}
	
	public boolean waitForLogin(){
		for (int i=0;i<LOGIN_WAIT_TIME*10;i++){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (sessionStatus == "CONNECTED"){
				return true;
			}
		}
		return false;
	}
	
}
