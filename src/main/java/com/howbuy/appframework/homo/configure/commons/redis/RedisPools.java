package com.howbuy.appframework.homo.configure.commons.redis;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.JedisPool;

/**
 * redis连接池.
 * @author li.zhang
 *
 */
public class RedisPools
{
    private Map<String, List<JedisPool>> pools;

    public Map<String, List<JedisPool>> getPools()
    {
        return pools;
    }

    public void setPools(Map<String, List<JedisPool>> pools)
    {
        this.pools = pools;
    }
    
    public static void main(String[] args)
    {
        System.out.println(new SecureRandom().nextInt(2));
    }
    
}
