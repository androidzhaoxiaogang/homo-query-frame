/**************************************************************************
 * $$RCSfile: Context.java $$  $$Revision: 1.1 $$  $$Date: 2010/01/19 02:05:44$$
 *
 * $$Log: Context.java$$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.context;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.howbuy.appframework.homo.configure.commons.container.ContainerException;

/** 
* @author kidd
* @version 1.0
*/
public interface Context{

	/**
	 * 
	 * @param name
	 * @return
	 * @throws ContainerException
	 */
	public SessionBean lookupSessionBean(String name) throws ContainerException;
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws ContainerException
	 */
	public Object lookupElementDef(String name) throws ContainerException;
	

	/**
	 * 根据dsId获取数据源信�?
	 * @param dsId
	 * @return
	 * @throws ContainerException
	 */
	public String[] lookupDataSource(String dsId) throws ContainerException;


	/**
	 * 返回初始化所有数据源信息
	 * @return
				data[0] = rs.getString("DRIVERS");
				data[1] = rs.getString("URL");
				data[2] = rs.getString("USERNAME");
				data[3] = rs.getString("PASSWD");
				data[4] = rs.getString("DS_ID");
				data[5] = rs.getString("RMI_IP");
				data[6] = rs.getString("RMI_ROUTING_RULE");
				data[7] = rs.getString("IS_POOL");
	 * @throws RemoteException
	 */
	public ArrayList<String[]> lookupDataSourceList() throws ContainerException;
}