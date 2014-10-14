/*
 * DZH DPS FILES
 * Created to 2012-10-16
 * Copyright 2012 
 */

package com.howbuy.appframework.homo.configure.commons.jdbc.rmi;

import java.rmi.RemoteException;

import com.howbuy.appframework.homo.configure.commons.util.ResultList;

public interface JdbcProxyProvider {
	/**
	 * DML或DDL操作
	 * @param dsId -- 数据源编号
	 * @param sql  -- 执行SQL
	 * @return
	 * 		1  = 正常
	 * 		-1 = 不正常
	 * @throws RemoteException
	 */
	public int updateExecute(String dsId,String sql) throws Exception;
	
	/**
	 * 查询操作
	 * @param dsId  -- 数据源编号
	 * @param sql   -- 执行SQL
	 * @return
	 * 		List<Object> result    = List<DynamicObject> 动态对象数据
	 * 		int dynamicColunmCount = 动态对象有效字段数
	 * @throws RemoteException
	 */
	public ResultList selectExecute(String dsId,String sql) throws Exception;

	
	/**
	 * 通过表名获得数据源dsId
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public String getSelectRightDsId(String groupId,String tableName) throws Exception;
}
