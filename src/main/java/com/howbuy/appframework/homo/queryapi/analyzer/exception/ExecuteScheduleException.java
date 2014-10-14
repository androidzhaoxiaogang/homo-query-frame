package com.howbuy.appframework.homo.queryapi.analyzer.exception;

/**
 * 执行执行计划异常的异常类
 * @author li.zhang
 *
 */
@SuppressWarnings("serial")
public class ExecuteScheduleException extends Exception
{
    /**
     * 默认构造方法
     */
    public ExecuteScheduleException()
    {
        
    }
    
    /**
     * 构造方法
     * @param message
     */
    public ExecuteScheduleException(String message)
    {
        super(message);
    }
}
