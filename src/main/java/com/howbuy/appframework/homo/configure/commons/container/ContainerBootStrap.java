/**************************************************************************
 * $$RCSfile: ContainerBootStrap.java $$  $$Revision: 1.1 $$  $$Date: 2010/01/19 02:05:44$$
 *
 * $$Log: ContainerBootStrap.java$$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.container;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.JedisPool;

import com.howbuy.appframework.homo.configure.commons.redis.ModeType;
import com.howbuy.appframework.homo.configure.commons.redis.RedisPools;
import com.howbuy.appframework.homo.configure.db.table.TableLoader;
import com.howbuy.appframework.homo.fincache.redis.cluster.RedisServManager;
import com.howbuy.appframework.homo.queryapi.QueryUtils;
import com.howbuy.appframework.homo.queryapi.cache.CacheLoadTask;
import com.howbuy.appframework.homo.queryapi.common.HomoProperty;
import com.howbuy.appframework.homo.queryapi.common.utils.HomoLogUtils;

/**
 * 容器启动类,在应用启动的时候触发其startup方法
 * @author kidd
 * @version
 */
public class ContainerBootStrap
{
    private static final Logger logger = Logger.getLogger(ContainerBootStrap.class);
    
    /** appContext **/
    private ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    
    public void load()
    {
        logger.info("Container begin load..................");
        loadRuntime();
        loadDataSource();
        loadProperties();
        loadRedisConnSource();//加载redis连接池.
        loadTable();//加载homo配置信息表
        logger.info("Container load success..................");
    }

    public void doRun()
    {
        logger.info("Container begin start..................");
        
        startTask();//开始任务列表
        addShutdownHook();//添加钩子.
        
        logger.info("Container start success...................");
    }

    public void loadProperties()
    {
        logger.info("[ContainerBootStrap]: start load properties");
        Properties props = new Properties();
        InputStream conf = ContainerBootStrap.class.getClassLoader().getResourceAsStream("conf.properties");
        try
        {
            props.load(conf);
            Enumeration<?> propertyNames = props.propertyNames();
            while (propertyNames.hasMoreElements())
            {
                String propertyName = (String)propertyNames.nextElement();
                System.setProperty(propertyName, props.getProperty(propertyName));
            }
            
            Connection conn = null;
            String dataSourceId = System.getProperty(HomoProperty.MONITOR_DB);
            try
            {
                conn = QueryUtils.getHomoConnection();
                PreparedStatement statement = conn.prepareStatement("select * from hm_datasource where ds_id = ?");
                statement.setString(1, dataSourceId);
                ResultSet rs = statement.executeQuery();
                if (rs.next())
                {
                    String driver = rs.getString("DRIVERS");
                    String url = rs.getString("URL");
                    String username = rs.getString("USERNAME");
                    String passwd = rs.getString("PASSWD");
                    
                    System.setProperty(HomoProperty.MONITOR_DB_DRIVER, driver);
                    System.setProperty(HomoProperty.MONITOR_DB_URL, url);
                    System.setProperty(HomoProperty.MONITOR_DB_USERNAME, username);
                    System.setProperty(HomoProperty.MONITOR_DB_PASSWORD, passwd);
                }
            }
            catch (SQLException e)
            {
                HomoLogUtils.printStackTrace(logger, e);
            }
            
        }
        catch (IOException e)
        {
            logger.error("load conf.properties file failed, error stack is " + e);
            HomoLogUtils.printStackTrace(logger, e);
        }
        
        logger.info("[ContainerBootStrap]: end load properties.");
    }

    public void loadTable()
    {
        logger.info("[ContainerBootStrap]: start load homo tables.");
        //通过loader加载相关的表.
        TableLoader.getInstance();
        
        logger.info("[ContainerBootStrap]: end load homo tables.");
    }
   
    /**
     * 初始化异步线程服
     */
    public void loadRuntime()
    {

    }

    /**
     * 数据容器加载初始化
     */
    public void loadDataSource()
    {
        logger.info("[ContainerBootStrap]: start load DataSource");
        Container container = ContainerFactory.getContainer();
        
        ArrayList<String[]> dataSourceList = new ArrayList<String[]>();
        try
        {
            DataSource ds = (DataSource)appContext.getBean("dataSource");
            container.addDataSource("dataSource", ds);
            
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT DRIVERS,URL,USERNAME,PASSWD,DS_ID FROM hm_datasource ");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
            {
                String[] data = new String[5];
                data[0] = rs.getString("DRIVERS");
                data[1] = rs.getString("URL");
                data[2] = rs.getString("USERNAME");
                data[3] = rs.getString("PASSWD");
                data[4] = rs.getString("DS_ID");
                dataSourceList.add(data);
            }
            
            container.addDataSourceList(dataSourceList);

            for (int i = 0; i < dataSourceList.size(); i++)
            {
                String[] values = (String[])dataSourceList.get(i);

                container.addDataSource(values[4], values);
            }
        }
        catch (Exception e)
        {
            logger.error("容器加载错误！", e);
            HomoLogUtils.printStackTrace(logger, e);
        }

        logger.info("[ContainerBootStrap]: end load DataSource");
    }
    
    /**
     * 加载redis连接池.
     */
    public void loadRedisConnSource()
    {
        logger.info("[ContainerBootStrap]: start load redis connection pool.");
        Container container = ContainerFactory.getContainer();
        RedisPools redisPools = (RedisPools)appContext.getBean("jedisPools");
        Map<String, List<JedisPool>> pools = redisPools.getPools();
        if (CollectionUtils.isEmpty(pools))
        {
            logger.info("[ContainerBootStrap]: redis pool is empty.");
            return;
        }
        
        Iterator<String> iterator = pools.keySet().iterator();
        while (iterator.hasNext())
        {
            String type = iterator.next();
            ModeType poolType = ModeType.get(type);
            List<JedisPool> jedisList = pools.get(type);
            
            if (CollectionUtils.isEmpty(jedisList))
            {
                continue;
            }
            
            switch (poolType)
            {
                case READ_ONLY:
                    container.addJedisPool(ModeType.READ_ONLY.getStrValue(), jedisList);
                    break;
                case WRITE_ONLY:
                    container.addJedisPool(ModeType.WRITE_ONLY.getStrValue(), jedisList);
                    break;
                case BOTH:
                    container.addJedisPool(ModeType.BOTH.getStrValue(), jedisList);
                    break;
            }
        }
        
        //redis管理器初始化.
        RedisServManager.getInstance();
        
        logger.info("[ContainerBootStrap]: end load redis connection pool.");
    }

    /**
     * 开始任务列表.
     */
    public void startTask()
    {
        CacheLoadTask task = (CacheLoadTask)appContext.getBean("cacheLoadTask");
        task.setDaemon(true);
        task.start();
    }
    
    /**
     * 添加应用停止时的钩子线程.
     */
    private void addShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(new HomoShutdownHook("shut-down-hook"));
    }
}