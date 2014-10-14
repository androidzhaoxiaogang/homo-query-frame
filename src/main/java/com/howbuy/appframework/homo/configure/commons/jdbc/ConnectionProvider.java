package com.howbuy.appframework.homo.configure.commons.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.howbuy.appframework.homo.configure.commons.jdbc.datasource.DriverManagerDataSource;
import com.mongodb.DB;


public abstract class ConnectionProvider {
	
	/**
	 * MongoDB
	 */
	public abstract DB getConnectionMongoDB(DriverManagerDataSource dataSource) throws Exception;
	
	public abstract void closeMongoDBConnection(DB db) throws SQLException;
	
	
	/**
	 * SQL
	 */
	/**
	 * Grab a connection
	 * @return a JDBC connection
	 * @throws SQLException
	 */
	public abstract Connection getConnection(DataSource dataSource) throws SQLException;
	
	/**
	 * Dispose of a used connection.
	 * @param conn a JDBC connection
	 * @throws SQLException
	 */
	public abstract void closeConnection(Connection conn) throws SQLException;

	public abstract void closePreparedStatement(PreparedStatement pstmt) throws SQLException;
	
	public abstract void closeResultSet(ResultSet rs) throws SQLException;
}
