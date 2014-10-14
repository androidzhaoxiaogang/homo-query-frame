package com.howbuy.appframework.homo.fincache.redis.joinquery;

public interface PriEntity
{
    /**
     * 主对象名称空间.
     * 
     * @return
     */
    String getnamespace();

    /**
     * 获得主对象key
     * 
     * @return 主对象key
     */
    String getKey();

    /**
     * 获得关联值.
     * 
     * @return 关联值
     */
    String getValue();
}
