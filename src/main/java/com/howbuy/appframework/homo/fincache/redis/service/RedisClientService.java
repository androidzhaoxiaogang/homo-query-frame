package com.howbuy.appframework.homo.fincache.redis.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

public class RedisClientService<K, V>
{
    private String[] servers;
    private List<RedisTemplateProxy<K, V>> redisTemplates = new ArrayList<RedisTemplateProxy<K, V>>();
    private boolean inited = false;
    private Set<JedisConnectionFactory> factorySet = new HashSet<JedisConnectionFactory>();
    private int timeout = 30000;
    private int reqcounter = 0;
    private Class<K> kCls;
    private Class<V> vCls;
    private static final Logger logger = Logger.getLogger(RedisClientService.class);
    
    public RedisClientService(Class<K> kType, Class<V> vType, String[] servers)
    {
        this.servers = servers;
        kCls = kType;
        vCls = vType;
        this.init();
    }

    /**
     * ԭ�Ӽ�����.
     * 
     * @param redisCounter
     * @return
     */
    public RedisAtomicInteger getRedisAtomicInteger(String redisCounter)
    {
        return new RedisAtomicInteger(redisCounter, getRedisTemplate().getConnectionFactory());
    }

    public RedisAtomicLong getRedisAtomicLong(String redisCounter)
    {
        return new RedisAtomicLong(redisCounter, getRedisTemplate().getConnectionFactory());
    }

    public RedisTemplate<K, V> getRedisTemplate()
    {
        int index = dispatchAlgorithm();
        
        //此时说明所有的redis都不可达.
        if (-1 == index)
        {
            return null;
        }
        return this.redisTemplates.get(index).getRedisTemplate();
    }
    

    /**
     * 分发算法，将redis请求分发到不同的redis机器上.
     * @return 返回要分发到的redis集群列表的index.
     */
    private int dispatchAlgorithm()
    {
        reqcounter++;
        reqcounter = reqcounter % 1000000;
        int length = redisTemplates.size();
        int index = 0;
        if (length >= 2)
        {
            index = reqcounter % length;
        }
        RedisTemplateProxy<K, V> proxy = redisTemplates.get(index);
        int counter = 0;
        while (!proxy.isValid() && counter < length)
        {
            counter++;
            reqcounter++;
            index = reqcounter % length;
            proxy = redisTemplates.get(index);
        }
        
        if (!proxy.isValid())
        {
            return -1;
        }
        return index;
    }
    
    
    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    private void init()
    {
        if (!inited)
        {
            logger.info("RedisClientService begin initializing....");
            for (int i = 0; i < servers.length; i++)
            {
                String addr = servers[i];
                String[] info = addr.split(":");
                String ip = info[0];
                int port = Integer.parseInt(info[1]);
                String auth = info[2];//redis密码
                
                JedisShardInfo shardInfo = new JedisShardInfo(ip, port, timeout);
                shardInfo.setPassword(auth);//redis客户端登陆后的密码, auth passwd,当前环境redis密码为howbuy.
                JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(shardInfo);
                jedisConnectionFactory.setUsePool(true);
                
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxActive(2);
                config.setTestOnBorrow(true);
                jedisConnectionFactory.setPoolConfig(config);
                // afterPropertiesSet
                jedisConnectionFactory.afterPropertiesSet();
                factorySet.add(jedisConnectionFactory);
                RedisTemplate<K, V> template = new RedisTemplate<K, V>();
                template.setConnectionFactory(jedisConnectionFactory);
                if (kCls.equals(String.class))
                {
                    template.setKeySerializer(new StringRedisSerializer());
                    template.setHashKeySerializer(new StringRedisSerializer());
                }
                else
                {
                    if (kCls.equals(byte[].class))
                    {
                        template.setKeySerializer(new BinRedisSerializer());
                        template.setHashKeySerializer(new BinRedisSerializer());
                    }
                    else
                    {
                        template.setKeySerializer(new JdkSerializationRedisSerializer());
                        template.setHashKeySerializer(new JdkSerializationRedisSerializer());
                    }
                }
                if (vCls.equals(String.class))
                {
                    template.setValueSerializer(new StringRedisSerializer());
                    template.setHashValueSerializer(new StringRedisSerializer());
                }
                else
                {
                    if (vCls.equals(byte[].class))
                    {
                        template.setValueSerializer(new BinRedisSerializer());
                        template.setHashValueSerializer(new BinRedisSerializer());
                    }
                    else
                    {
                        template.setValueSerializer(new JdkSerializationRedisSerializer());
                        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
                    }
                }
                template.setDefaultSerializer(new JdkSerializationRedisSerializer());
                // template.setKeySerializer(new ShihuaRedisSerializer());
                // template.setHashKeySerializer(new ShihuaRedisSerializer());
                // template.setValueSerializer(new ShihuaRedisSerializer());
                // template.setHashValueSerializer(new ShihuaRedisSerializer());
                RedisTemplateProxy<K, V> proxy = new RedisTemplateProxy<K, V>();
                proxy.setRedisTemplate(template);
                redisTemplates.add(proxy);
            }
            
            logger.info("RedisClientService end initialize...");
            
            monitorThread.setDaemon(true);//守护线程，主程序关闭时一起关闭.
            monitorThread.start();
            inited = true;
        }
    }

    private static final byte[] ECHOBYTES = "msg".getBytes();
    private Thread monitorThread = new Thread()
    {
        public void run()
        {
            this.setName("monitorThread");
            while (!stopped)
            {
                for (RedisTemplateProxy<K, V> proxy : redisTemplates)
                {
                    RedisConnection conn = null;
                    try
                    {
                        conn = proxy.getRedisTemplate().getConnectionFactory().getConnection();
                        byte[] rtnMsg = conn.echo(ECHOBYTES);
                        if (rtnMsg == null || rtnMsg.length != ECHOBYTES.length)
                        {
                            proxy.setValid(false);
                        }
                        else
                        {
                            proxy.setValid(true);
                        }
                    }
                    catch (Exception ex)
                    {
                        proxy.setValid(false);
                    }
                    finally
                    {
                        if (null != conn)
                        {
                            conn.close();
                        }
                    }
                }
                
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    break;//TODO ??
                }
            }
            for (JedisConnectionFactory factory : factorySet)
            {
                try
                {
                    factory.destroy();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    };
    private boolean stopped = false;

    public void stop()
    {
        stopped = true;
    }

    public String[] getServers()
    {
        return servers;
    }

    public void setServers(String[] servers)
    {
        this.servers = servers;
    }
}
