package com.howbuy.appframework.homo.queryapi.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.howbuy.appframework.homo.queryapi.DataService;
import com.howbuy.appframework.homo.queryapi.DataServiceImpl;
import com.howbuy.appframework.homo.queryapi.JsonQueryResp;
import com.howbuy.appframework.homo.queryapi.common.utils.HomoLogUtils;

@SuppressWarnings("serial")
public class CommonJsonQuery extends HttpServlet
{
    
    /** 数据服务 **/
    private DataService dataService = new DataServiceImpl();
    
    /** 日志. **/
    private static final Logger LOGGER = Logger.getLogger(CommonJsonQuery.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        JsonQueryResp queryResp = new JsonQueryResp();
        
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        String queryStr = request.getParameter("query");
        LOGGER.info("Query string is : " + queryStr);
        queryStr = URLDecoder.decode(queryStr, "utf-8").trim();
        
        //TODO li.zhang 前端接入后续需要加个队列, 否则请求积压的情况下，可能会崩掉.
        
        try
        {
            queryStr = URLDecoder.decode(queryStr, "utf-8").trim();
            LOGGER.info("Decoded query string is : " + queryStr);
	        queryResp = dataService.query(queryStr);
            queryResp.setResultCode("0000");
            queryResp.setResultMsg("success");
        }
        catch (Exception ex)
        {
            HomoLogUtils.printStackTrace(LOGGER, ex);
            onError(ex, queryResp, response);
            return;
        }
        
        JSONObject jsonResp = JSONObject.fromObject(queryResp);
        PrintWriter out = response.getWriter();
        out.println(jsonResp.toString());
        
        LOGGER.info(jsonResp.toString());
    }

    /**
     * 异常处理
     * @param ex
     * @param response
     * @throws IOException 
     * @throws UnsupportedEncodingException 
     */
    private void onError(Exception ex, JsonQueryResp queryResp, HttpServletResponse response) 
        throws UnsupportedEncodingException, IOException
    {
        HomoLogUtils.printStackTrace(LOGGER, ex);
        JSONObject jsonResp = JSONObject.fromObject(queryResp);
        response.getWriter().println(jsonResp.toString());
    }
}
