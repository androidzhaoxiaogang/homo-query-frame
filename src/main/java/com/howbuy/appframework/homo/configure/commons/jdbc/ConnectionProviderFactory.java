package com.howbuy.appframework.homo.configure.commons.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.howbuy.appframework.homo.configure.commons.jdbc.datasource.AbstractDataSource;
import com.howbuy.appframework.homo.configure.commons.jdbc.datasource.DriverManagerDataSource;
import com.howbuy.appframework.homo.configure.commons.util.PropertiesUtil;
import com.mongodb.DB;


public final class ConnectionProviderFactory {
	
	private static final Logger logger = Logger.getLogger(DriverManagerConnectionProvider.class);
	
    private static ConnectionProvider connectionProvider = null;
	
    public synchronized static ConnectionProvider newConnectionProvider() {
        if(connectionProvider == null) {
        	connectionProvider = (ConnectionProvider) DriverManagerConnectionProvider.getInstance();
        }
        return connectionProvider;
    }
	
    public static Connection getConnection(String dsId){
    	try {
    		AbstractDataSource dataSource = (AbstractDataSource) DriverManagerDataSource.getInstance(dsId);
			return connectionProvider.getConnection(dataSource);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    public static DB getConnectionMongoDB(String dsId){
    	try {
    		DriverManagerDataSource dataSource = (DriverManagerDataSource)DriverManagerDataSource.getInstance(dsId);
			return connectionProvider.getConnectionMongoDB(dataSource);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    public static Connection getConnection(){
    	String dsId = PropertiesUtil.getPropertiesValue("jdbc.dsId");
		return getConnection(dsId);
    }
    
	public static void closeAll(Connection conn,PreparedStatement pstmt,ResultSet rs){

	
		closeResultSet(rs);
		closePreparedStatement(pstmt);
		closeConnection(conn);
	}
	
	public static void closeConnection(Connection conn){
		try{
			logger.debug("closing JDBC connection");
			connectionProvider.closeConnection(conn);
		}catch(SQLException e){
			logger.error("Could not closing connection", e);
		}
	}
	
	public static void closeMongoDBConnection(DB db){
		try{
			logger.debug("closing JDBC connection for MogonDB");
			connectionProvider.closeMongoDBConnection(db);
		}catch(SQLException e){
			logger.error("Could not closing connection for MogonDB", e);
		}
	}
	
	public static void closePreparedStatement(PreparedStatement pstmt){
		try{
			logger.debug("closing JDBC PreparedStatement");
			connectionProvider.closePreparedStatement(pstmt);
		}catch(SQLException e){
			logger.error("Could not closing PreparedStatement", e);
		}
	}
	
	public static void closeResultSet(ResultSet rs){
		try{
			logger.debug("closing JDBC ResultSet");
			connectionProvider.closeResultSet(rs);
		}catch(SQLException e){
			logger.error("Could not closing ResultSet", e);
		}
	}
}
