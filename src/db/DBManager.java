package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import utils.Logger;

public class DBManager {
	
	String db = "fxdb";
	Connection conn;
	String project_location = "C:\\users\\shubham\\git\\fx\\";
	
	public DBManager(){

		    try {
		    	
			    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
				Class.forName(driver);
			    
				String url = "jdbc:derby:" + project_location + db;
				conn = DriverManager.getConnection(url);
				Logger.info("Connected to " + db);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void close(){
		Logger.info("Disconnecting from " + db);
		try {
			conn.close();
			String url = "jdbc:derby:;shutdown=true";
			DriverManager.getConnection(url);
		} catch (SQLException e) {
			if (e.getErrorCode() == 50000){
				return;
			}
			e.printStackTrace();
		}
	}
	
	public ResultSet readData(String sql){
		try {
			return conn.createStatement().executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void writeData(String sql){
		Statement s;
		try {
			s = conn.createStatement();
			s.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printData(String sql){
		ResultSet rs = readData(sql);
		try {
		    ResultSetMetaData rsmd = rs.getMetaData();
		    int columnsNumber = rsmd.getColumnCount();
		    while (rs.next()) {
		        for (int i = 1; i <= columnsNumber; i++) {
		            if (i > 1) System.out.print(",  ");
		            String columnValue = rs.getString(i);
		            System.out.print(columnValue + " " + rsmd.getColumnName(i));
		        }
		        System.out.println("");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public LinkedList<String> getRelatedPairs(String currency){
		ResultSet rs = readData("select name from pairs where base='" + currency + "' or quote = '" + currency + "'");
		LinkedList<String> res = new LinkedList<String>();
		try {
			while(rs.next()){
				res.add(rs.getString(1));
			}
			return res;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
//	public static void main(String[] args){
//		DBManager dm = new DBManager();
//		//dm.printData("select * from pairs");
//		dm.close();
//	}
	
	
	
}
