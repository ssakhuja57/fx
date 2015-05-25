package com.peebeekay.fx.utils.config;

public abstract class DBConfig{
	
	protected String dbHost;
	protected int dbPort;
	protected String dbName;
	protected String dbUser;
	protected String dbPass;

	
	public DBConfig(String dbHost, int dbPort, String dbName, String dbUser, String dbPass){
		this.dbHost = dbHost;
		this.dbPort = dbPort;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPass = dbPass;
	}
	
	// getters
	public String getDBHost(){
		return dbHost;
	}
	public int getDBPort(){
		return dbPort;
	}
	public String getDBName(){
		return dbName;
	}
	public String getDBUser(){
		return dbUser;
	}
	public String getDBPass(){
		return dbPass;
	}
	
	public abstract String getDriverClass();
	public abstract String getJDBCString();
}
