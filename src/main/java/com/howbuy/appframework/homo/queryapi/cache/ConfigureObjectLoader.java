package com.howbuy.appframework.homo.queryapi.cache;

import org.apache.log4j.Logger;

import com.howbuy.appframework.homo.queryapi.db.QueryService;

public class ConfigureObjectLoader implements CacheLoader
{
    private QueryService queryService;

    public QueryService getQueryService()
    {
        return queryService;
    }

    public void setQueryService(QueryService queryService)
    {
        this.queryService = queryService;
    }

    private Logger logger = Logger.getLogger(this.getClass());

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void load2cache()
    {

    }

}
