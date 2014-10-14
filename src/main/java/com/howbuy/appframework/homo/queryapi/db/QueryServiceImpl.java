/*
 * DZH DPS FILES
 * Created to 2014-5-20
 * Copyright 2012 
 */

package com.howbuy.appframework.homo.queryapi.db;

import org.apache.log4j.Logger;

public class QueryServiceImpl implements QueryService
{

    /**
	 * 
	 */
    private static Logger logger = Logger.getLogger(QueryServiceImpl.class);

    private CommonDAO commonDAO;

    /**
     * @return the commonDAO
     */
    public CommonDAO getCommonDAO()
    {
        return commonDAO;
    }

    /**
     * @param commonDAO
     *            the commonDAO to set
     */
    public void setCommonDAO(CommonDAO commonDAO)
    {
        this.commonDAO = commonDAO;
    }

    @Override
    public Object[] updateExecute(String dsId, String sql)
    {
        StringBuilder message = new StringBuilder();
        message.append("[QueryServiceImpl] Object[] updateExecute(").append(dsId).append(", ").append(sql).append(")");
        logger.info(message);
        return commonDAO.updateExecute(null, dsId, sql);
    }

    @Override
    public Object[] selectExecute(String dsId, String sql)
    {
        StringBuilder message = new StringBuilder();
        message.append("[QueryServiceImpl] Object[] selectExecute(").append(dsId).append(", ").append(sql).append(")");
        logger.info(message);
        return commonDAO.selectExecute(null, dsId, sql);
    }

}
