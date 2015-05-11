package com.peebeekay.fx.session;

public class Credentials {

	private String login;
	private String password;
	private String demoOrReal;
		public static final String DEMO = "Demo";
		public static final String REAL = "Real";
	private String[] accountNumbers;
	
	public Credentials(String login, String password, String demoOrReal, String[] accountNumbers){
		this.login = login;
		this.password = password;
		this.demoOrReal = demoOrReal;
		this.accountNumbers = accountNumbers;
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
