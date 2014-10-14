package com.howbuy.appframework.homo.fincache.redis.cluster;

import java.security.SecureRandom;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.JedisPool;

import com.howbuy.appframework.homo.configure.commons.container.Container;
import com.howbuy.appframework.homo.configure.commons.container.ContainerBootStrap;
import com.howbuy.appframework.homo.configure.commons.container.ContainerFactory;
import com.howbuy.appframework.homo.fincache.redis.service.RedisClientService;
import com.howbuy.appframework.homo.queryapi.common.Symbols;

/**
 * redis服务管理.
 * @author li.zhang
 *
 */
public class RedisServManager
{
    private static final Logger logger = Logger.getLogger(ContainerBootStrap.class);
    
    /** 单例. **/
    private static final RedisServManager INSTANCE = new RedisServManager();
    
    /** redis服务. **/
    private final RedisClientService<String, String> service;
    
    /** redisHost:redisPort:password **/
    private static final String REDIS_DEFAULT_HOST_CONF = "192.168.220.105:6479:howbuy,192.168.220.105:6579:howbuy";
    
    /**
     * 私有构造方法
     */
    private RedisServManager()
    {
        String servers = System.getProperty("redis.hosts.conf", REDIS_DEFAULT_HOST_CONF);
        logger.info("redis host is " + servers);
        
        String[] redisServers = servers.split(Symbols.COMMA, -1);
        this.service = new RedisClientService<String, String>(String.class, String.class, redisServers);
    }
    
    /**
     * 单例模式.
     * @return
     */
    public static RedisServManager getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * 获取到redis的连接.
     */
    public RedisConnection getRedisConnection()
    {
        RedisConnection conn = null;
        if (null == service.getRedisTemplate())
        {
            return conn;
        }
        
        conn = service.getRedisTemplate().getConnectionFactory().getConnection();
        return conn;
    }
    
    /**
     * 获取redis连接池
     * @param poolType 连接池类型.
     * @return
     */
    public JedisPool getJedisClientProxy(String poolType)
    {
        JedisPool pool = null;
        Container container = ContainerFactory.getContainer();
        List<JedisPool> list = container.getJedisPool(poolType);
        if (CollectionUtils.isEmpty(list))
        {
            return pool;
        }
        
        //选择其中的一个连接池返回.. 选择的策略可以是基于负载均衡或者只是简单的返回其中的一个任意元素.目前先简单处理.
        return select(list);
    }

    /**
     * 从中选择一个连接池返回
     * @param poolList poolList
     * @return 连接池
     */
    private JedisPool select(List<JedisPool> poolList)
    {
        return poolList.get(new SecureRandom().nextInt(poolList.size()));
    }
}
