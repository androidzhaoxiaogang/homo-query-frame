package com.howbuy.appframework.homo.queryapi.analyzer;


/**
 * 执行计划优化器
 * @author li.zhang
 *
 */
public class ScheduleOptimizer
{
    /** 实例. **/
    private static final ScheduleOptimizer INSTANCE = new ScheduleOptimizer();
    
    /**
     * 获取单例
     * @return
     */
    public static ScheduleOptimizer getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * 优化执行计划
     * @param schedule 执行计划
     */
    public void optimize(ExecSchedule schedule)
    {

    }

}
