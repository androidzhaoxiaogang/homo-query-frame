/*
 * DZH DPS FILES
 * Created to 2014-5-16
 * Copyright 2012 
 */

package com.howbuy.appframework.homo.queryapi;

public interface DataService
{
    /**
     * 返回查询结果集.
     * @param queryJsonStr 前端传入的json请求字符串.
     * @return 返回查询结果集.
     */
    JsonQueryResp query(String queryJsonStr) throws Exception;

}
