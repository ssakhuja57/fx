package com.peebeekay.fx.session;

import java.util.HashMap;
import java.util.Map;

public class Credentials {

	private String login;
	private String password;
	private String demoOrReal;
		public static final String DEMO = "Demo";
		public static final String REAL = "Real";
	private String[] accountNumbers;
	
	public enum LoginProperties{
		AUTO_RECONNECT_ATTEMPTS;
	}
	private Map<LoginProperties, String> props = new HashMap<LoginProperties, String>();
	
	public Credentials(String login, String password, String demoOrReal, String[] accountNumbers){
		this.login = login;
		this.password = password;
		this.demoOrReal = demoOrReal;
		this.accountNumbers = accountNumbers;
	}
	
	public void setProperty(LoginProperties prop, String value){
		props.put(prop, value);
	}
	
	public String getProperty(LoginProperties prop){
		return props.get(prop);
	}
	
	// getters
	public String getLogin(){
		return login;
	}
	public String getPassword(){
		return password;
	}
	public String getDemoOrReal(){
		return demoOrReal;
	}
	public String[] getAccountNumbers(){
		return accountNumbers;
	}

}
