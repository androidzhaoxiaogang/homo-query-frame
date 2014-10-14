/**************************************************************************
 * $$RCSfile: ContainerImpl.java $$  $$Revision: 1.1 $$  $$Date: 2010/01/19 02:05:44$$
 *
 * $$Log: ContainerImpl.java$$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.container;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisPool;
import com.howbuy.appframework.homo.configure.commons.context.Context;
import com.howbuy.appframework.homo.configure.commons.context.ContextImpl;
import com.howbuy.appframework.homo.configure.commons.context.SessionBean;
import com.howbuy.appframework.homo.configure.commons.context.SessionBeanImpl;

/**
 * 
 * @author kidd
 * 
 * @version 1.0
 * 
 */
public class ContainerImpl implements Container
{

    private static ContainerImpl container = null;

    private Hashtable<String, String> sessionBeanTable = new Hashtable<String, String>();

    private Hashtable<String, String> elementDefTable = new Hashtable<String, String>();

    private Hashtable<String, String[]> dataSourceTable = new Hashtable<String, String[]>();

    private Hashtable<String, DataSource> dataSources = new Hashtable<String, DataSource>();
    
    private Hashtable<String, List<JedisPool>> jedisPools = new Hashtable<String, List<JedisPool>>();

    private ArrayList<String[]> dataSourceListTable = new ArrayList<String[]>();
    
    protected Logger logger = Logger.getLogger(ContainerImpl.class);

    ContainerImpl()
    {
        // this(DEFAULTDB_NAME);
        container = this;
        ContainerFactory.setContainer(this);
    }

    public Context newContext()
    {
        ContextImpl context = new ContextImpl();
        context.setContainer(this);
        logger.debug("create new context");
        return context;
    }

    public static ContainerImpl getInstance()
    {
        if (container == null)
        {
            return new ContainerImpl();
        }
        else
        {
            return container;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.taxation.common.container.Container#addSessionBean(java.lang.String,
     * java.lang.String)
     */
    public void addSessionBean(String beanName, String beanInfo)
    {
        sessionBeanTable.put(beanName, beanInfo);
        logger.info("SessionBean added:" + beanName);
    }

    public void addElementDef(String beanName, String beanInfo)
    {
        elementDefTable.put(beanName, beanInfo);
        logger.info("ElementDef added:" + beanName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gxlu.ietools.basic.system.container.Container#addBobject(java.lang.String
     * , java.lang.Object)
     */
    public void addDataSource(String dsId, String[] dsValue)
    {
        dataSourceTable.put(dsId, dsValue);
        logger.info("DataSource added:" + dsId);
    }

    @Override
    public void addDataSourceList(ArrayList<String[]> dataSourceList)
    {
        // TODO Auto-generated method stub
        this.dataSourceListTable = dataSourceList;
        logger.info("DataSourceList added:" + dataSourceList);
    }

    /**
     * Method lookupSessionBean.
     * 
     * @param sessionName
     * @return SessionBean
     * @throws ContainerException
     */
    public SessionBean lookupSessionBean(String beanName) throws ContainerException
    {
        String beanValue = (String) sessionBeanTable.get(beanName);
        try
        {
            Class<?> c = Class.forName(beanValue);
            Constructor<?> constructor = c.getConstructor();
            SessionBeanImpl sessionBean = (SessionBeanImpl)constructor.newInstance((Object[]) null);
            logger.debug("Session Bean created:" + beanName);
            return sessionBean;
        }
        catch (Exception e)
        {
            throw new ContainerException(e.getClass().getName() + ":" + e.getMessage());
        }
    }

    public Object lookupElementDef(String name) throws ContainerException
    {
        String beanValue = (String)elementDefTable.get(name);
        if (beanValue == null)
        {
            return null;
        }
        else
        {
            try
            {
                Class<?> c = Class.forName(beanValue);
                Constructor<?> constructor = c.getConstructor();
                Object baseInterface = constructor.newInstance((Object[]) null);
                logger.debug("BaseInterface created:" + name);
                return baseInterface;
            }
            catch (Exception e)
            {
                throw new ContainerException(e.getClass().getName() + ":" + e.getMessage());
            }
        }

    }

    public String[] lookupDataSource(String dsId) throws ContainerException
    {
        String[] c = (String[])dataSourceTable.get(dsId);
        try
        {
            return c;
        }
        catch (Exception e)
        {
            throw new ContainerException(e.getClass().getName() + ":" + e.getMessage());
        }
    }

    public ArrayList<String[]> lookupDataSourceList() throws ContainerException
    {
        try
        {
            return dataSourceListTable;
        }
        catch (Exception e)
        {
            throw new ContainerException(e.getClass().getName() + ":" + e.getMessage());
        }
    }
    
    /**
     * 添加数据源
     * @param dsName 数据源名称
     * @param dataSource 数据源
     */
    public void addDataSource(String dsName ,DataSource dataSource)
    {
        Hashtable<String, DataSource> dsHashTbl = new Hashtable<String, DataSource>();
        if (!CollectionUtils.isEmpty(this.dataSources))
        {
            dsHashTbl.putAll(dataSources);
        }
        
        dsHashTbl.put(dsName, dataSource);
        this.dataSources = dsHashTbl;
    }
    
    /**
     * 获取数据源
     * @param dsName 数据源名称
     * @return
     */
    public DataSource getDataSource(String dsName)
    {
        return dataSources.get(dsName);
    }
    
    /**
     * 添加redis连接池
     * @param poolType 连接池的类型， 是redis的读的连接池，还是redis写的连接池，或者是既能读有能写的连接池.
     * @param jedisPool redis连接池.
     */
    public void addJedisPool(String poolType, JedisPool jedisPool)
    {
        Hashtable<String, List<JedisPool>> pools = new Hashtable<String, List<JedisPool>>();
        if (!CollectionUtils.isEmpty(this.jedisPools))
        {
            pools.putAll(this.jedisPools);
        }
        
        List<JedisPool> temp = pools.get(poolType);
        if (CollectionUtils.isEmpty(temp))
        {
            temp = new ArrayList<JedisPool>();
            pools.put(poolType, temp);
        }
        
        temp.add(jedisPool);
        
        this.jedisPools = pools;
    }
    
    
    /**
     * 添加redis连接池
     * @param poolType 连接池类型
     * @param jedisPools jedisPools
     */
    public void addJedisPool(String poolType, List<JedisPool> jedisPools)
    {
        Hashtable<String, List<JedisPool>> pools = new Hashtable<String, List<JedisPool>>();
        if (!CollectionUtils.isEmpty(this.jedisPools))
        {
            pools.putAll(this.jedisPools);
        }
        
        List<JedisPool> temp = pools.get(poolType);
        if (CollectionUtils.isEmpty(temp))
        {
            temp = new ArrayList<JedisPool>();
            pools.put(poolType, temp);
        }
        
        temp.addAll(jedisPools);
        
        this.jedisPools = pools;
    }
    
    /**
     * 获取连接池
     * @param poolType 连接池的类型.
     * @return
     */
    public List<JedisPool> getJedisPool(String poolType)
    {
        return this.jedisPools.get(poolType);
    }
}
