/*
 * DZH DPS FILES
 * Created to 2012-10-15
 * Copyright 2012 
 */

package com.howbuy.appframework.homo.configure.commons.jdbc.pool;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;


public class DynamicDataSourcePool {
	
	 protected Logger logger = Logger.getLogger(DynamicDataSourcePool.class);
	 private ComboPooledDataSource pool=null;//����C3p0���l�ӳر�
	 
	 /**
	  * Ĭ�ϵĹ��췽��
	  * @param userName  ��ݿ��û���
	  * @param pass      ��ݿ�����
	  * @param url       l�ӵ�url
	  * @param driverClass  �����
	  */
	 public DynamicDataSourcePool(String userName,String pass,String url,String driverClass) {
	  try {
	   this.pool=new ComboPooledDataSource();//��������
	   this.pool.setDriverClass(driverClass);//������
	   this.pool.setJdbcUrl(url); //����l�ӵ�url
	   this.pool.setUser(userName);//������ݿ��û���
	   this.pool.setPassword(pass);//������ݿ�����
	   this.pool.setAcquireIncrement(5);//��l�ӳ��е�l�Ӻľ���ʱ��c3p0һ��ͬʱ��ȡ��l����
	   this.pool.setAutoCommitOnClose(false);//l�ӹر�ʱĬ�Ͻ�����δ�ύ�Ĳ���ع�
	   this.pool.setBreakAfterAcquireFailure(false);//��ȡl��ʧ�ܺ�����Դ�������ѶϿ����>ùر�
	   this.pool.setCheckoutTimeout(1000);//��l�ӳ�����ʱ�ͻ��˵���getConnection()��ȴ��ȡ��l�ӵ�ʱ�䣬��ʱ���׳�SQLException,����Ϊ0�������ڵȴ�λ���롣
	   this.pool.setIdleConnectionTestPeriod(60);//ÿ60��������l�ӳ��еĿ���l��
	   this.pool.setInitialPoolSize(300);//��ʼ��ʱ��ȡ10��l�ӣ�ȡֵӦ��minPoolSize��maxPoolSize֮��
	   this.pool.setMaxPoolSize(40);//l�ӳ��б�������l����
	   this.pool.setMinPoolSize(100);//l�ӳ���Сl����
	   this.pool.setMaxIdleTime(600);//������ʱ��,60����δʹ����l�ӱ�����
	   this.pool.setNumHelperThreads(3);//c3p0���첽����ģ������JDBC����ͨ���������ɡ�)չ��Щ���������Ч����������ͨ����߳�ʵ�ֶ�����ͬʱ��ִ��
	  } catch (PropertyVetoException e) {
	   e.printStackTrace();
	  }
	 }
	 
	    /**
	    * �õ�l��
	    * @return
	    */
	 public Connection  getConnection(){
	  try {
	   return this.pool.getConnection();
	  } catch (SQLException e) {
		  logger.info("��ȡl���쳣");
	   e.printStackTrace();
	  }
	  return null;
	 }
	 /**
	  * �ر�
	  */
	 public void destroy(){
	  if(null!=this.pool)this.pool.close();
	 }
}
