/*
 * DZH DPS FILES
 * Created to 2014-5-20
 * Copyright 2012 
 */

package com.howbuy.appframework.homo.queryapi.db;

import org.apache.log4j.Logger;

import com.howbuy.appframework.homo.configure.commons.jdbc.rmi.JdbcProxyProvider;

public class CommonDAO
{
    /**
	 * 
	 */
    private static Logger logger = Logger.getLogger(CommonDAO.class);

    private JdbcProxyProvider jdbcProxyProvider;

    /**
     * @return the jdbcProxyProvider
     */
    public JdbcProxyProvider getJdbcProxyProvider()
    {
        return jdbcProxyProvider;
    }

    /**
     * @param jdbcProxyProvider
     *            the jdbcProxyProvider to set
     */
    public void setJdbcProxyProvider(JdbcProxyProvider jdbcProxyProvider)
    {
        this.jdbcProxyProvider = jdbcProxyProvider;
    }

    public CommonDAO()
    {

    }

    /**
     * 杩滅▼閫氳繃JdbcProxy鎵цSQL澧炲垹鏀�
     * 
     * @param groupId
     *            鏁版嵁婧愬垎缁勭紪鍙凤紝瀛樺湪灏嗛噰鐢ㄥ垎甯冨紡杩涜鎿嶄綔
     * @param content
     *            濡俫roupId涓虹┖=dsId锛涘groupId涓嶄负绌�琛ㄥ悕绉�
     * @param sql
     *            鎵цSQL璇彞
     * @return Object[0] = true | false Object[1] = 1 = 姝ｅ父; -1 = 涓嶆甯�
     * @throws RemoteException
     */
    public Object[] updateExecute(String groupId, String content, String sql)
    {
        Object[] rObjs = new Object[2];
        rObjs[0] = Boolean.TRUE;
        // TODO Auto-generated method stub
        String dsId = null;
        try
        {
            if (groupId == null)
            {
                dsId = content;
            }

            logger.info("[JdbcProxyProvider] - [DataSource]: " + dsId
                    + " | Object[] updateExecute(" + groupId + ", " + content
                    + ", " + sql + ")");

            rObjs[1] = jdbcProxyProvider.updateExecute(dsId, sql);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            rObjs[0] = Boolean.FALSE;
            rObjs[1] = null;
            logger.error(e);
        }
        return rObjs;
    }

    /**
     * 杩滅▼閫氳繃JdbcProxy鎵цSQL鏌ヨ
     * 
     * @param groupId 鏁版嵁婧愬垎缁勭紪鍙凤紝瀛樺湪灏嗛噰鐢ㄥ垎甯冨紡杩涜鎿嶄綔
     * @param content 濡俫roupId涓虹┖=dsId锛涘groupId涓嶄负绌�琛ㄥ悕绉�
     * @param sql 鎵цSQL璇彞
     * @return Object[0] = true | false Object[1] = List<String[][]> 澶氱淮鏁版嵁缁撴灉闆�
     * @throws RemoteException
     */
    public Object[] selectExecute(String groupId, String content, String sql)
    {
        Object[] rObjs = new Object[2];
        rObjs[0] = Boolean.TRUE;

        String dsId = null;
        try
        {
            if (groupId == null)
            {
                dsId = content;
            }

            StringBuilder message = new StringBuilder();
            message.append("[JdbcProxyProvider] - [DataSource]: ").append(dsId)
                   .append(" | Object[] selectExecute(").append(groupId)
                   .append(", ").append(content)
                   .append(", ").append(sql).append(")");
            logger.info(message);

            rObjs[1] = jdbcProxyProvider.selectExecute(dsId, sql);
        }
        catch (Exception e)
        {
            rObjs[0] = Boolean.FALSE;
            rObjs[1] = null;
            logger.error(e);
        }
        return rObjs;
    }
}
