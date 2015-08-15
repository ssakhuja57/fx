package com.peebeekay.fx.utils.config;

public class PostgresConfig extends DBConfig{

	public PostgresConfig(String dbHost, int dbPort, String dbName,
			String dbUser, String dbPass) {
		super(dbHost, dbPort, dbName, dbUser, dbPass);
		
	}

	@Override
	public String getDriverClass() {
		return "org.postgresql.Driver";
	}

	@Override
	public String getJDBCString() {
		return "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;
	}

}
