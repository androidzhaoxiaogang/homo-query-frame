/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.howbuy.appframework.homo.configure.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.howbuy.appframework.homo.configure.commons.jdbc.CannotGetJdbcConnectionException;
import com.howbuy.appframework.homo.configure.commons.util.ExectionUtil;
import com.howbuy.appframework.homo.configure.commons.util.PropertiesUtil;
import com.howbuy.appframework.homo.configure.commons.util.StringUtils;
import com.mongodb.DB;
import com.mongodb.Mongo;


public class DriverManagerDataSource extends AbstractDataSource {

	private String driverClassName;

	private String url;

	private String username;

	private String password;

	private String dsId;
	
	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	/**
	 * Constructor for bean-style configuration.
	 */
	public DriverManagerDataSource(){
		
	}
	
	private static DriverManagerDataSource dmds = null;
	
	public static DriverManagerDataSource getInstance(String dsId) {
		if (dmds == null)
			return new DriverManagerDataSource(dsId);
		else
			return dmds;
	}

	/**
	 * 
	 * @return
	 */
	private Connection getBasicConnection(){
		Connection conn = null;
		try{
			if(getIsPool()){
				Context ctx = new InitialContext();
				DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
				return ds.getConnection();
			}else{
				return getConnectionFromDriverManager(PropertiesUtil.getPropertiesValue("jdbc.driverClassName"), PropertiesUtil.getPropertiesValue("jdbc.url"), PropertiesUtil.getPropertiesValue("jdbc.username"), PropertiesUtil.getPropertiesValue("jdbc.password"));
			}

		} catch(SQLException e){
			logger.error("Could not load JDBC driver class ",e);
		} catch(NamingException e1) {
			logger.error("Could not load JDBC driver class ",e1);
		}
		return conn;
	}
	
	/**
	 * Create a new DriverManagerDataSource with the given standard
	 * DriverManager parameters.
	 * @param driverClassName the JDBC driver class name
	 * @param url the JDBC URL to use for accessing the DriverManager
	 * @param username the JDBC username to use for accessing the DriverManager
	 * @param password the JDBC password to use for accessing the DriverManager
	 * @see java.sql.DriverManager#getConnection(String, String, String)
	 */
	public DriverManagerDataSource(String driverClassName, String url, String username, String password)
			throws CannotGetJdbcConnectionException {
		setDriverClassName(driverClassName);
		setUrl(url);
		setUsername(username);
		setPassword(password);
	}

	/**
	 * Create a new DriverManagerDataSource with the given standard
	 * DriverManager parameters.
	 * @param url the JDBC URL to use for accessing the DriverManager
	 * @param username the JDBC username to use for accessing the DriverManager
	 * @param password the JDBC password to use for accessing the DriverManager
	 * @see java.sql.DriverManager#getConnection(String, String, String)
	 */
	public DriverManagerDataSource(String url, String username, String password)
			throws CannotGetJdbcConnectionException {
		setUrl(url);
		setUsername(username);
		setPassword(password);
	}


	public DriverManagerDataSource(String dsId){
		setDsId(dsId);
	}


	/**
	 * Set the JDBC driver class name. This driver will get initialized
	 * on startup, registering itself with the JDK's DriverManager.
	 * <p>Alternatively, consider initializing the JDBC driver yourself
	 * before instantiating this DataSource.
	 * @see Class#forName(String)
	 * @see java.sql.DriverManager#registerDriver(java.sql.Driver)
	 */
	public void setDriverClassName(String driverClassName) throws CannotGetJdbcConnectionException {
		if (!StringUtils.hasText(driverClassName)) {
			throw new IllegalArgumentException("driverClassName must not be empty");
		}
		this.driverClassName = driverClassName.trim();
		try {
			Class.forName(this.driverClassName, true, null);
		}
		catch (ClassNotFoundException ex) {
			throw new CannotGetJdbcConnectionException(
					"Could not load JDBC driver class [" + this.driverClassName + "]", ex);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Loaded JDBC driver: " + this.driverClassName);
		}
	}

	/**
	 * Return the JDBC driver class name, if any.
	 */
	public String getDriverClassName() {
		return driverClassName;
	}

	/**
	 * Set the JDBC URL to use for accessing the DriverManager.
	 * @see java.sql.DriverManager#getConnection(String, String, String)
	 */
	public void setUrl(String url) {
		if (!StringUtils.hasText(url)) {
			throw new IllegalArgumentException("url must not be empty");
		}
		this.url = url.trim();
	}

	/**
	 * Return the JDBC URL to use for accessing the DriverManager.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Set the JDBC username to use for accessing the DriverManager.
	 * @see java.sql.DriverManager#getConnection(String, String, String)
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Return the JDBC username to use for accessing the DriverManager.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set the JDBC password to use for accessing the DriverManager.
	 * @see java.sql.DriverManager#getConnection(String, String, String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Return the JDBC password to use for accessing the DriverManager.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * This implementation delegates to <code>getConnectionFromDriverManager</code>,
	 * using the default username and password of this DataSource.
	 * @see #getConnectionFromDriverManager()
	 */
	public Connection getConnection() {
		try {
			return getConnectionFromDriverManager();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	public Connection getConnection(String arg0, String arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public DB getConnectionFromMongoDB() throws Exception {
		// TODO Auto-generated method stub
		return getConnectionFromDriverManagerMongoDB(getBasicConnection());
	}
	
	/**
	 * Get a Connection from the DriverManager,
	 * using the default username and password of this DataSource.
	 * @see #getConnectionFromDriverManager(String, String)
	 */
	protected Connection getConnectionFromDriverManager() throws Exception {
		return getConnectionFromDriverManager(getBasicConnection());
	}

	/**
	 * Build properties for the DriverManager, including the given username
	 * and password (if any).
	 * @see #getConnectionFromDriverManager(String, java.util.Properties)
	 */
	protected Connection getConnectionFromDriverManager(Connection conn)
	    throws Exception {

		String[] dataArray = getConnectionFromStringArr(conn);
		
		if(getIsPool()){
			return getBasicConnection();
		}else{
			return getConnectionFromDriverManager(dataArray[0], dataArray[1], dataArray[2], dataArray[3]);
		}
	}
	
	/**
	 * 获取MongoDB的链接对象
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	protected DB getConnectionFromDriverManagerMongoDB(Connection conn) throws Exception {
		String[] dataArray = getConnectionFromStringArr(conn);
				
		String[] mongoDBConn = dataArray[1].split(",");
		
		Mongo m = new Mongo(mongoDBConn[0],(new Integer(mongoDBConn[1])).intValue());
		
		DB db = m.getDB(mongoDBConn[2]);
				
		if(dataArray[2]!=null&&!dataArray[2].equals("")&&dataArray[3]!=null&&!dataArray[3].equals("")){
			
			char[] pwd = dataArray[3].toCharArray();
			
			db.authenticate(dataArray[2], pwd);
		}
				
		if(mongoDBConn.length>3&&mongoDBConn[3]!=null&&!mongoDBConn[3].equals("")){

			db = m.getDB(mongoDBConn[3]);
		}
			
		return db;
	}

	/**
	 * 返回数据库获取的4个目标链接库参数
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	protected String[] getConnectionFromStringArr(Connection conn) throws Exception{
		if(getIsPool()){
			com.howbuy.appframework.homo.configure.commons.context.Context context = ExectionUtil.getContext();
			return context.lookupDataSource(getDsId());
		}else{
			PreparedStatement pstmt = conn.prepareStatement("SELECT DRIVERS,URL,USERNAME,PASSWD FROM HM_DATASOURCE WHERE DS_ID = "+getDsId());
			ResultSet rs = pstmt.executeQuery();
			
			String[] data = new String[4];
			if(rs.next()){
				data[0] = rs.getString("DRIVERS");
				data[1] = rs.getString("URL");
				data[2] = rs.getString("USERNAME");
				data[3] = rs.getString("PASSWD");
			}
		
			return data;
		}
	}
	
	/**
	 * Getting a connection using the nasty static from DriverManager is extracted
	 * into a protected method to allow for easy unit testing.
	 * @see java.sql.DriverManager#getConnection(String, java.util.Properties)
	 */
	protected Connection getConnectionFromDriverManager(String drivers, String url, String userName, String passWord)
	    throws SQLException {

		if (logger.isDebugEnabled()) {
			logger.debug("Creating new JDBC Connection to [" + url + "]");
		}
		
		Connection conn = null;
		try{
			Class.forName(drivers);
			conn = DriverManager.getConnection(url, userName, passWord);
		} catch(SQLException e){
			logger.error("Could not load JDBC driver class ",e);
		} catch(ClassNotFoundException e1) {
			logger.error("Could not load JDBC driver class ",e1);
		}
		return conn;
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean getIsPool(){
		//如果dsId为configration，并且开启连接池模式，则使用连接池
		if(PropertiesUtil.getPropertiesValue("jdbc.dsId").equals(getDsId())&&new Boolean(PropertiesUtil.getPropertiesValue("jdbc.isConnectionPool")).booleanValue()){
			return true;
		}else{
			return false;
		}
	}
}
