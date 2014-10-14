package com.howbuy.appframework.homo.queryapi.cache;

import com.howbuy.appframework.homo.queryapi.parser.redis.RCCommander;


/**
 * 缓存管理器
 * @author li.zhang
 *
 */
public final class CacheManager
{
    private static CacheManager INSTANCE = new CacheManager();
    
    private CacheManager()
    {
        
    }
    
    public static CacheManager getInstance()
    {
        return INSTANCE;
    }
    
    
    /**
     * getCacheDispatcher
     * @return
     */
    public CacheDispatcher getCacheDispatcher()
    {
        return new CacheDispatcher(new RCCommander());
    }
}
