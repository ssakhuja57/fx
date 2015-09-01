package com.peebeekay.fx.listeners;
import com.fxcore2.IO2GSessionStatus;
import com.fxcore2.O2GSessionStatusCode;
import com.peebeekay.fx.brokers.fxcm.FxcmSessionManager;
import com.peebeekay.fx.session.Credentials.LoginProperties;
import com.peebeekay.fx.utils.Logger;


public class SessionStatusListener implements IO2GSessionStatus{
	
	O2GSessionStatusCode sessionStatus;
	private final int LOGIN_WAIT_TIME = 10;
	
	private FxcmSessionManager session;
	
	public SessionStatusListener(FxcmSessionManager session) {
		this.session = session;
	}
	
	@Override
	public void onLoginFailed(String arg0) {
		Logger.error("Unable to login:");
		Logger.error(arg0);
	}

	@Override
	public void onSessionStatusChanged(O2GSessionStatusCode statusCode) {
		sessionStatus = statusCode;
		Logger.info(sessionStatus.toString());
		if(statusCode == O2GSessionStatusCode.DISCONNECTED || statusCode == O2GSessionStatusCode.SESSION_LOST){
			String reconnectAttemptsProp = session.getLoginProperty(LoginProperties.AUTO_RECONNECT_ATTEMPTS);
			if(reconnectAttemptsProp == null)
				return;
			int reconnectAttempts = Integer.parseInt(reconnectAttemptsProp);
			int attempts = 0;
			while(attempts < reconnectAttempts){
				session.close();
				session.connect();
				if(waitForLogin()){
					session.reconnect();
					return;
				}
				attempts++;
			}
		}
	}
	
	public boolean waitForLogin(){
		for (int i=0;i<LOGIN_WAIT_TIME*10;i++){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (sessionStatus == O2GSessionStatusCode.CONNECTED){
				return true;
			}
		}
		return false;
	}
	
}
