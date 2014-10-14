/**************************************************************************
 * $$RCSfile: Container.java $$  $$Revision: 1.1 $$  $$Date: 2010/01/19 02:05:44$$
 *
 * $$Log: Container.java$$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.container;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import redis.clients.jedis.JedisPool;
import com.howbuy.appframework.homo.configure.commons.context.Context;

/** 
 *
 * @author Kidd
 * @version 1.0
 */
public interface Container {

    /**
     * 容器控制器
     * @return
     */
    public abstract Context newContext();
    
    /**
     * ���ػỰ������
     * @param beanName
     * @param beanInfo
     */
    public abstract void addSessionBean(String beanName, String beanInfo);
    
    /**
     * ����Ԫ�ع��˻����漰��
     * @param beanName
     * @param beanInfo
     */
    public abstract void addElementDef(String beanName, String beanInfo);
    

    /**
     * 加载数据源数据到容器
     * @param dsId
     * @param dsValue
     */
    public abstract void addDataSource(String dsId, String[] dsValue);

    /**
     * 加载数据源列表到容器
     * @param dataSourceList
     */
    public abstract void addDataSourceList(ArrayList<String[]> dataSourceList);

    /**
     * 添加数据源
     * @param dsName 数据源名称
     * @param dataSource 数据源
     */
    public abstract void addDataSource(String dsName, DataSource dataSource);
    
    /**
     * 获取数据源
     * @param dsName 数据源名称
     * @return
     */
    public abstract DataSource getDataSource(String dsName);
    
    /**
     * 添加redis连接池
     * @param poolType 连接池的类型， 是redis的读的连接池，还是redis写的连接池，或者是既能读有能写的连接池.
     * @param jedisPool redis连接池.
     */
    public abstract void addJedisPool(String poolType, JedisPool jedisPool);
    
    /**
     * 添加redis连接池
     * @param poolType 连接池类型
     * @param jedisPools jedisPools
     */
    public abstract void addJedisPool(String poolType, List<JedisPool> jedisPools);
    
    /**
     * 获取连接池
     * @param poolType 连接池的类型.
     * @return
     */
    public abstract List<JedisPool> getJedisPool(String poolType);

}
