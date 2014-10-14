/*
 * DZH DPS FILES
 * Created to 2012-10-15
 * Copyright 2012 
 */

package com.howbuy.appframework.homo.configure.commons.jdbc.rmi.client;

import com.howbuy.appframework.homo.configure.commons.util.ResultList;

public interface ISQLExecute{ 


	/**
	 * 查询操作
	 * @param dsId  -- 数据源编号
	 * @param sql   -- 执行SQL
	 * @return
	 * @throws RemoteException
	 */
	public int updateExecute(String dsId,String sql) throws Exception;
	
	/**
	 * 查询操作
	 * @param dsId  -- 数据源编号
	 * @param sql   -- 执行SQL
	 * @return
	 * @throws RemoteException
	 */
	public ResultList selectExecute(String dsId,String sql) throws Exception;
}