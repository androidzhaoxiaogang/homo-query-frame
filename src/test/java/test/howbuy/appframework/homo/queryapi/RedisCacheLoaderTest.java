/*
 * DZH DPS FILES
 * Created to 2014-5-20
 * Copyright 2012 
 */

package test.howbuy.appframework.homo.queryapi;

import com.howbuy.appframework.homo.queryapi.db.QueryService;

public class RedisCacheLoaderTest extends TestBase
{
    private QueryService queryService;

    @Override
    protected void initialize() throws Exception
    {
        queryService = (QueryService)applicationContext.getBean("queryService");
    }

    public final void testConnection()
    {
        Object[] seleConn = queryService.selectExecute("102", "select count(0) from dual");
        assertEquals(true, ((Boolean) seleConn[0]).booleanValue());
    }

}
