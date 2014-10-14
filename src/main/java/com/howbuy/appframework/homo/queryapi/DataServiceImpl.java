package com.howbuy.appframework.homo.queryapi;

import org.apache.log4j.Logger;

import com.gw.globe.GlobeObjectRef;
import com.howbuy.appframework.homo.queryapi.analyzer.ExecSchedule;
import com.howbuy.appframework.homo.queryapi.analyzer.JsonResolver;
import com.howbuy.appframework.homo.queryapi.analyzer.ScheduleExecutor;
import com.howbuy.appframework.homo.queryapi.analyzer.ScheduleOptimizer;
import com.howbuy.appframework.homo.queryapi.analyzer.exception.CreateExecScheduleException;
import com.howbuy.appframework.homo.queryapi.analyzer.exception.ExecuteScheduleException;
import com.howbuy.appframework.homo.queryapi.db.QueryService;

public class DataServiceImpl implements DataService
{
    private static final Logger LOGGER = Logger.getLogger(QueryUtils.class);
    
    private QueryService queryService;

    static
    {
        try
        {
            if (GlobeObjectRef.getBean("dataService") == null)
            {
                GlobeObjectRef.setBean("dataService", new DataServiceImpl());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * 返回查询结果集.
     * @param queryJsonStr 前端传入的json请求字符串.
     * @return 返回查询结果集.
     */
    @Override
    public JsonQueryResp query(String queryJsonStr) throws Exception
    {
        //首先, 构造执行计划.
        ExecSchedule schedule = doCreateSchedule(queryJsonStr);
        
        //其次，优化执行计划.
        ScheduleOptimizer.getInstance().optimize(schedule);
        
        //最后, 计划执行器执行解析后的计划.
        JsonQueryResp rs = doExecSchedule(schedule);
        
        return rs;
    }

    /**
     * 创建执行计划.
     * @param queryJsonStr json格式的查询请求
     * @return 执行计划
     * @throws CreateExecScheduleException
     */
    private ExecSchedule doCreateSchedule(String queryJsonStr) throws CreateExecScheduleException
    {
        LOGGER.info(" create execution schedule begin...............");
        long begin = System.currentTimeMillis();
        ExecSchedule schedule = JsonResolver.getInstance().createExecSchedule(queryJsonStr);
        long end = System.currentTimeMillis();
        LOGGER.info(" create execution schedule end, cost " + (end - begin) / 1000.0 + " seconds................");
        return schedule;
    }
    
    /**
     * 执行执行计划.
     * @param schedule 执行计划.
     * @return 执行后返回的结果集.
     * @throws ExecuteScheduleException
     */
    private JsonQueryResp doExecSchedule(ExecSchedule schedule) throws ExecuteScheduleException
    {
        LOGGER.info(" execute schedule begin.................");
        long begin = System.currentTimeMillis();
        JsonQueryResp rs = ScheduleExecutor.getInstance().execute(schedule);
        long end = System.currentTimeMillis();
        LOGGER.info(" execute schedule end, cost " + (end - begin) / 1000.0 + " seconds................");
        return rs;
    }
    
    public QueryService getQueryService()
    {
        return queryService;
    }

    public void setQueryService(QueryService queryService)
    {
        this.queryService = queryService;
    }

    private boolean trace = true;

    public boolean isTrace()
    {
        return trace;
    }

    public void setTrace(boolean trace)
    {
        this.trace = trace;
    }
}
