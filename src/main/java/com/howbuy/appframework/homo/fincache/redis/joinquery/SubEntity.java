package com.howbuy.appframework.homo.fincache.redis.joinquery;

import java.util.Map;

public interface SubEntity
{
    /**
     * 获得名称空间.
     * 
     * @return 名称空间
     */
    String getnamespace();

    /**
     * 获得对象key
     * 
     * @return key
     */
    String getKey();

    /**
     * 获得属性集合.
     * 
     * @return 属性集合
     */
    Map<String, String> getValueMap();
}
