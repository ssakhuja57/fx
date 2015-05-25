package com.peebeekay.fx.utils.config;

public class VerticaConfig extends DBConfig{

	public VerticaConfig(String dbHost, int dbPort, String dbName,
			String dbUser, String dbPass) {
		super(dbHost, dbPort, dbName, dbUser, dbPass);
	}

	@Override
	public String getDriverClass() {
		return "com.vertica.jdbc.Driver";
	}

	@Override
	public String getJDBCString() {
		return "jdbc:vertica://" + dbHost + ":" + dbPort + "/" + dbName + "?user=" + dbUser + "&password=" + dbPass;
	}
	
	
}
