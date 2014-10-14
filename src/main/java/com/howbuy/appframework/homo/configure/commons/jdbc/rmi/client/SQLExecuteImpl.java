/*
 * DZH DPS FILES
 * Created to 2012-10-15
 * Copyright 2012 
 */

package com.howbuy.appframework.homo.configure.commons.jdbc.rmi.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.howbuy.appframework.homo.configure.commons.context.Context;
import com.howbuy.appframework.homo.configure.commons.jdbc.pool.DynamicDataSourcePool;
import com.howbuy.appframework.homo.configure.commons.jdbc.pool.DynamicDataSourcePoolFactory;
import com.howbuy.appframework.homo.configure.commons.util.DynamicObject;
import com.howbuy.appframework.homo.configure.commons.util.ExectionUtil;
import com.howbuy.appframework.homo.configure.commons.util.ResultList;

public class SQLExecuteImpl implements ISQLExecute
{

    private static final long serialVersionUID = 1L;
    
    protected static Logger logger = Logger.getLogger(SQLExecuteImpl.class);

    protected static DynamicDataSourcePoolFactory factory = null;

    protected static Context context = null;
    
    static
    {
        factory = DynamicDataSourcePoolFactory.getInstance();
        context = ExectionUtil.getContext();
    }

    public SQLExecuteImpl()
    {

    }

    @Override
    public int updateExecute(String dsId, String sql) throws Exception
    {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try
        {
            logger.info("[Execute Process] -- {[DataSource]:" + dsId + " | [Thread ID]:" + Thread.currentThread().getId() + "}");
            DynamicDataSourcePool dataSourcePool = factory.getDynamicDataSourcePool(dsId);
            if (dataSourcePool == null)
            {
                logger.info("[Execute Process] -- [DataSource]:" + dsId + ",lookup datasource in context.");
                String[] sources = context.lookupDataSource(dsId);

                logger.info("[Execute Process] -- [DataSource]:" + dsId + ",trying to get db connection......");
                Class.forName(sources[0]);
                connection = DriverManager.getConnection(sources[1], sources[2], sources[3]);
                logger.info("[Execute Process] -- [DataSource]:" + dsId + ",get db connection succeed......");
            }
            else
            {
                connection = dataSourcePool.getConnection();
            }
            
            logger.info("[Execute Process] -- [DataSource]:" + dsId + " | [SQL]:" + sql);
            pstmt = connection.prepareStatement(sql);
            return pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            logger.error("[SQLException] -- [DataSource]:" + dsId, e);
        }
        catch (ClassNotFoundException e)
        {
            logger.error("[ClassNotFoundException] -- [DataSource]:" + dsId, e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                pstmt.close();
                connection.close();
                logger.info("[Close Process] -- [DataSource]:" + dsId);
            }
            catch (SQLException e)
            {
                logger.error("[Close Process] -- [DataSource]:" + dsId, e);
            }

        }

        return 0;
    }

    @Override
    public ResultList selectExecute(String dsId, String sql) throws Exception
    {
        ResultList resultList = new ResultList();

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            logger.info("[Execute Process] -- {[DataSource]:" + dsId + " | [Thread ID]:" + Thread.currentThread().getId() + "}");
            DynamicDataSourcePool dataSourcePool = factory.getDynamicDataSourcePool(dsId);
            if (dataSourcePool == null)
            {
                logger.info("[Execute Process] -- [DataSource]:" + dsId + ",lookup datasource in context.");
                String[] sources = context.lookupDataSource(dsId);

                logger.info("[Execute Process] -- [DataSource]:" + dsId + ",trying to get db connection......");
                Class.forName(sources[0]);
                connection = DriverManager.getConnection(sources[1], sources[2], sources[3]);
                logger.info("[Execute Process] -- [DataSource]:" + dsId + ",get db connection succeed......");
            }
            else
            {
                connection = dataSourcePool.getConnection();
            }
            logger.info("[Execute Process] -- [DataSource]:" + dsId + " | [SQL]:" + sql);
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            ResultSetMetaData rsd = rs.getMetaData();
            int columnCount = rsd.getColumnCount();
            resultList.setDynamicColunmCount(columnCount);

            //设置查询结果的表头
            String[] dynamicColunmHead = new String[columnCount];
            for (int i = 0; i < columnCount; i++)
            {
                dynamicColunmHead[i] = rsd.getColumnName(i + 1);
            }
            resultList.setDynamicColunmHead(dynamicColunmHead);

            List<Object> dList = new ArrayList<Object>();
            while (rs.next())
            {
                DynamicObject object = new DynamicObject();
                setDynamicColumnValue(columnCount, object, rs);
                dList.add(object);
            }
            resultList.setResult(dList);
        }
        catch (SQLException e)
        {
            logger.error("[SQLException] -- [DataSource]:" + dsId, e);
        }
        catch (ClassNotFoundException e)
        {
            logger.error("[ClassNotFoundException] -- [DataSource]:" + dsId, e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                pstmt.close();
                connection.close();
                logger.info("[Close Process] -- [DataSource]:" + dsId);
            }
            catch (SQLException e)
            {
                logger.error("[Close Process] -- [DataSource]:" + dsId, e);
            }

        }

        return resultList;
    }

    /**
     * @param columnCount
     * @param object
     * @param rs
     * @throws SQLException
     */
    private static void setDynamicColumnValue(int columnCount, DynamicObject object, ResultSet rs) throws SQLException
    {
        for (int i = 0; i < columnCount; i++)
        {
            if (i == 0)
            {
                object.setF0(rs.getString(i + 1));
            }
            else if (i == 1)
            {
                object.setF1(rs.getString(i + 1));
            }
            else if (i == 2)
            {
                object.setF2(rs.getString(i + 1));
            }
            else if (i == 3)
            {
                object.setF3(rs.getString(i + 1));
            }
            else if (i == 4)
            {
                object.setF4(rs.getString(i + 1));
            }
            else if (i == 5)
            {
                object.setF5(rs.getString(i + 1));
            }
            else if (i == 6)
            {
                object.setF6(rs.getString(i + 1));
            }
            else if (i == 7)
            {
                object.setF7(rs.getString(i + 1));
            }
            else if (i == 8)
            {
                object.setF8(rs.getString(i + 1));
            }
            else if (i == 9)
            {
                object.setF9(rs.getString(i + 1));
            }
            else if (i == 10)
            {
                object.setF10(rs.getString(i + 1));
            }
            else if (i == 11)
            {
                object.setF11(rs.getString(i + 1));
            }
            else if (i == 12)
            {
                object.setF12(rs.getString(i + 1));
            }
            else if (i == 13)
            {
                object.setF13(rs.getString(i + 1));
            }
            else if (i == 14)
            {
                object.setF14(rs.getString(i + 1));
            }
            else if (i == 15)
            {
                object.setF15(rs.getString(i + 1));
            }
            else if (i == 16)
            {
                object.setF16(rs.getString(i + 1));
            }
            else if (i == 17)
            {
                object.setF17(rs.getString(i + 1));
            }
            else if (i == 18)
            {
                object.setF18(rs.getString(i + 1));
            }
            else if (i == 19)
            {
                object.setF19(rs.getString(i + 1));
            }
            else if (i == 20)
            {
                object.setF20(rs.getString(i + 1));
            }
            else if (i == 21)
            {
                object.setF21(rs.getString(i + 1));
            }
            else if (i == 22)
            {
                object.setF22(rs.getString(i + 1));
            }
            else if (i == 23)
            {
                object.setF23(rs.getString(i + 1));
            }
            else if (i == 24)
            {
                object.setF24(rs.getString(i + 1));
            }
            else if (i == 25)
            {
                object.setF25(rs.getString(i + 1));
            }
            else if (i == 26)
            {
                object.setF26(rs.getString(i + 1));
            }
            else if (i == 27)
            {
                object.setF27(rs.getString(i + 1));
            }
            else if (i == 28)
            {
                object.setF28(rs.getString(i + 1));
            }
            else if (i == 29)
            {
                object.setF29(rs.getString(i + 1));
            }
            else if (i == 30)
            {
                object.setF30(rs.getString(i + 1));
            }
            else if (i == 31)
            {
                object.setF31(rs.getString(i + 1));
            }
            else if (i == 32)
            {
                object.setF32(rs.getString(i + 1));
            }
            else if (i == 33)
            {
                object.setF33(rs.getString(i + 1));
            }
            else if (i == 34)
            {
                object.setF34(rs.getString(i + 1));
            }
            else if (i == 35)
            {
                object.setF35(rs.getString(i + 1));
            }
            else if (i == 36)
            {
                object.setF36(rs.getString(i + 1));
            }
            else if (i == 37)
            {
                object.setF37(rs.getString(i + 1));
            }
            else if (i == 38)
            {
                object.setF38(rs.getString(i + 1));
            }
            else if (i == 39)
            {
                object.setF39(rs.getString(i + 1));
            }
            else if (i == 40)
            {
                object.setF40(rs.getString(i + 1));
            }
            else if (i == 41)
            {
                object.setF41(rs.getString(i + 1));
            }
            else if (i == 42)
            {
                object.setF42(rs.getString(i + 1));
            }
            else if (i == 43)
            {
                object.setF43(rs.getString(i + 1));
            }
            else if (i == 44)
            {
                object.setF44(rs.getString(i + 1));
            }
            else if (i == 45)
            {
                object.setF45(rs.getString(i + 1));
            }
            else if (i == 46)
            {
                object.setF46(rs.getString(i + 1));
            }
            else if (i == 47)
            {
                object.setF47(rs.getString(i + 1));
            }
            else if (i == 48)
            {
                object.setF48(rs.getString(i + 1));
            }
            else if (i == 49)
            {
                object.setF49(rs.getString(i + 1));
            }
            else if (i == 50)
            {
                object.setF50(rs.getString(i + 1));
            }
            else if (i == 51)
            {
                object.setF51(rs.getString(i + 1));
            }
            else if (i == 52)
            {
                object.setF52(rs.getString(i + 1));
            }
            else if (i == 53)
            {
                object.setF53(rs.getString(i + 1));
            }
            else if (i == 54)
            {
                object.setF54(rs.getString(i + 1));
            }
            else if (i == 55)
            {
                object.setF55(rs.getString(i + 1));
            }
            else if (i == 56)
            {
                object.setF56(rs.getString(i + 1));
            }
            else if (i == 57)
            {
                object.setF57(rs.getString(i + 1));
            }
            else if (i == 58)
            {
                object.setF58(rs.getString(i + 1));
            }
            else if (i == 59)
            {
                object.setF59(rs.getString(i + 1));
            }
            else if (i == 50)
            {
                object.setF60(rs.getString(i + 1));
            }
        }
    }
}