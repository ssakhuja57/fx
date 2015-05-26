package com.peebeekay.fx.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import com.peebeekay.fx.utils.config.DBConfig;

public class DBUtils {


	public static Connection getConn(DBConfig config) throws SQLException{
		try{
			Class.forName(config.getDriverClass());	
		} catch (ClassNotFoundException e){
			Logger.error("driver class not found");
		}
		String connString = config.getJDBCString();
		Connection conn = DriverManager.getConnection(connString);
		return conn;
	}
	
	public static ArrayList<String[]> readQuery(DBConfig config, String sql) throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Connection conn = getConn(config);
		Statement st = null;
		ResultSet rs = null;
		st = conn.createStatement();
		rs = st.executeQuery(sql);
		int columnCount = rs.getMetaData().getColumnCount();
		
		try{
			while(rs.next()){
				String[] row = new String[columnCount];
				int i = 0;
				while(i < columnCount){
					int colType = rs.getMetaData().getColumnType(i+1);
					if(colType == Types.TIMESTAMP)
						row[i] = rs.getTimestamp(i+1).toString();
					else{
						row[i] = rs.getString(i+1);
					}
					i++;
				}
				al.add(row);
			}
		}
		finally{
			rs.close();
			conn.close();
		}
		return al;
	}
	
	public static String readSingleResult(DBConfig config, String sql) throws SQLException{
		return readQuery(config, sql).get(0)[0];
	}
	
	public static int getRowCount(DBConfig config, String table) throws SQLException{
		String sql = "select count(*) from " + table;
		return Integer.parseInt(readSingleResult(config, sql));
	}
	
}
