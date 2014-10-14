/*
 * DZH DPS FILES
 * Created to 2012-10-15
 * Copyright 2012 
 */

package com.howbuy.appframework.homo.configure.commons.jdbc.pool;

import java.util.Hashtable;

public class DynamicDataSourcePoolFactory
{
    private static Hashtable<String, DynamicDataSourcePool> hashtable = null;
    private static DynamicDataSourcePoolFactory dataSourcePoolFactory;

    private DynamicDataSourcePoolFactory()
    {
    }

    public static DynamicDataSourcePoolFactory getInstance()
    {
        if (null == dataSourcePoolFactory)
        {
            hashtable = new Hashtable<String, DynamicDataSourcePool>();
            dataSourcePoolFactory = new DynamicDataSourcePoolFactory();
        }
        return dataSourcePoolFactory;
    }

    /**
     * ��l�ӳ�
     * 
     * @param key
     *            l�ӳص���Ʊ���Ψһ
     * @param dataSourcePool
     *            ��Ӧ��l�ӳ�
     */
    public void bind(String key, DynamicDataSourcePool dataSourcePool)
    {
        if (IsBePool(key))
        {
            getDynamicDataSourcePool(key).destroy();
        }
        hashtable.put(key, dataSourcePool);
    }

    /**
     * ���°�l�ӳ�
     * 
     * @param key key
     * @param dataSourcePool dataSourcePool
     */
    public void rebind(String key, DynamicDataSourcePool dataSourcePool)
    {
        if (IsBePool(key))
        {
            getDynamicDataSourcePool(key).destroy();
        }
        hashtable.put(key, dataSourcePool);
    }

    /**
     * ɾ��̬���l�ӳ������Ϊkey��l�ӳ�
     * 
     * @param key
     */
    public void unbind(String key)
    {
        if (IsBePool(key))
        {
            getDynamicDataSourcePool(key).destroy();
        }
        hashtable.remove(key);
    }

    /**
     * ���Ҷ�̬���l�ӳ����Ƿ�������Ϊkey��l�ӳ�
     * 
     * @param key
     * @return
     */
    public boolean IsBePool(String key)
    {
        return hashtable.containsKey(key);
    }

    /**
     * ���key����key��Ӧ��l�ӳ�
     * 
     * @param key
     * @return
     */
    public DynamicDataSourcePool getDynamicDataSourcePool(String key)
    {
        if (!IsBePool(key))
        {
            return null;
        }
        return (DynamicDataSourcePool) hashtable.get(key);

    }
}
