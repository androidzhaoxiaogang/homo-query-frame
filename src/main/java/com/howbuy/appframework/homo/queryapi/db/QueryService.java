/*
 * DZH DPS FILES
 * Created to 2014-5-20
 * Copyright 2012 
 */

package com.howbuy.appframework.homo.queryapi.db;

public interface QueryService {
	
	/**
	 * 远程通过JdbcProxy执行SQL增删改
	 * 
	 * @param dsId     数据源编号
	 * @param sql      执行SQL语句
	 * @return
	 * 		Object[0] = true | false
	 * 		Object[1] = 1  = 正常; -1 = 不正常
	 */
	public Object[] updateExecute(String dsId, String sql);
	
	/**
	 * 远程通过JdbcProxy执行SQL查询
	 * 
	 * @param dsId     数据源编号
	 * @param sql      执行SQL语句
	 * @return
	 * 		Object[0] = true | false
	 * 		Object[1] = List<String[][]> 多维数据结果集
	 */
	public Object[] selectExecute(String dsId, String sql);
}
