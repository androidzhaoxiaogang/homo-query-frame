/*
 * DZH DPS FILES
 * Created to 2012-10-16
 * Copyright 2012 
 */

package com.howbuy.appframework.homo.configure.commons.jdbc.rmi;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.howbuy.appframework.homo.configure.commons.context.Context;
import com.howbuy.appframework.homo.configure.commons.jdbc.rmi.client.ISQLExecute;
import com.howbuy.appframework.homo.configure.commons.jdbc.rmi.client.SQLExecuteImpl;
import com.howbuy.appframework.homo.configure.commons.util.ExectionUtil;
import com.howbuy.appframework.homo.configure.commons.util.MD5Util;
import com.howbuy.appframework.homo.configure.commons.util.ResultList;

public class JdbcProxyProviderImpl implements JdbcProxyProvider
{

    private static final Logger logger = Logger
            .getLogger(JdbcProxyProviderImpl.class);

    private static JdbcProxyProviderImpl jdbcProxyProvider = null;

    private static ISQLExecute sqlExecute = null;

    public static JdbcProxyProviderImpl getInstance()
    {
        if (jdbcProxyProvider == null)
            return new JdbcProxyProviderImpl();
        else
            return jdbcProxyProvider;
    }

    static
    {
        sqlExecute = new SQLExecuteImpl();
    }

    @Override
    public int updateExecute(String dsId, String sql) throws Exception
    {
        try
        {
            return sqlExecute.updateExecute(dsId, sql);
        }
        catch (MalformedURLException e)
        {
            logger.error(e);
        }
        return 0;
    }

    @Override
    public ResultList selectExecute(String dsId, String sql) throws Exception
    {
        try
        {
            return sqlExecute.selectExecute(dsId, sql);
        }
        catch (MalformedURLException e)
        {
            logger.error(e);
        }
        return null;
    }

    @Override
    public String getSelectRightDsId(String groupId, String tableName) throws Exception
    {
        String dsId = null;

        Context context = ExectionUtil.getContext();
        ArrayList<String[]> dataSourceList = context.lookupDataSourceList();

        String contentToMD5toFirst = MD5Util.MD5(tableName).substring(0, 1);
        System.out.println("contentToMD5toFirst: " + contentToMD5toFirst);
        System.out.println("MD5Util.MD5(tableName): " + MD5Util.MD5(tableName));
        System.out.println("tableName: " + tableName);

        outer: for (int i = 0; i < dataSourceList.size(); i++)
        {
            String[] d = (String[]) dataSourceList.get(i);
            if (d[4].substring(0, 1).equals(groupId))
            {
                String[] ruleValues = d[6].split(",");
                for (int j = 0; j < ruleValues.length; j++)
                {
                    logger.info("getSelectRightDsId(" + groupId + ","
                            + tableName + ") | [ruleValues]:" + ruleValues[j]);

                    if (ruleValues[j].equals(contentToMD5toFirst))
                    {
                        dsId = d[4];
                        break outer;
                    }
                }
            }
        }

        return dsId;
    }

}
