package com.howbuy.appframework.homo.configure.commons.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.howbuy.appframework.homo.configure.commons.jdbc.datasource.DriverManagerDataSource;
import com.mongodb.DB;
import com.mongodb.Mongo;

public class DriverManagerConnectionProvider extends ConnectionProvider {

	private static final Logger logger = Logger.getLogger(DriverManagerConnectionProvider.class);
	
	private static DriverManagerConnectionProvider dmcp = null;
	
	public static DriverManagerConnectionProvider getInstance() {
		if (dmcp == null)
			return new DriverManagerConnectionProvider();
		else
			return dmcp;
	}
	
	@Override
	public Connection getConnection(DataSource dataSource) throws SQLException {
		// TODO Auto-generated method stub
		if (dataSource != null) {
			logger.debug("opening new JDBC connection");
			return dataSource.getConnection();
		} else {
			logger.error("error new JDBC connection");
			return null;
		}
	}

	@Override
	public void closeConnection(Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		if (conn != null) {
			conn.close();
		}
		logger.debug("closing JDBC connection");
	}
	
	@Override
	public void closeMongoDBConnection(DB db) throws SQLException {
		// TODO Auto-generated method stub
		if (db != null) {
			Mongo m = db.getMongo();
			m.close();
		}
		logger.debug("closing JDBC connection for MongoDB");
	}

	@Override
	public void closePreparedStatement(PreparedStatement pstmt) throws SQLException {
		// TODO Auto-generated method stub
		if(pstmt!=null){
			pstmt.close();
		}
		logger.debug("closing Connection PreparedStatement");
	}

	@Override
	public void closeResultSet(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		if(rs!=null){
			rs.close();
		}
		logger.debug("closing Connection ResultSet");
	}

	@Override
	public DB getConnectionMongoDB(DriverManagerDataSource dataSource) throws Exception {
		// TODO Auto-generated method stub
		if (dataSource != null) {
			logger.debug("opening new JDBC connection");
			return dataSource.getConnectionFromMongoDB();
		} else {
			logger.error("error new JDBC connection");
			return null;
		}
	}

}
