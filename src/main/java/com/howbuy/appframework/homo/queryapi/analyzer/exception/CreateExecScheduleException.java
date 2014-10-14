package com.howbuy.appframework.homo.queryapi.analyzer.exception;

/**
 * 创建执行计划异常异常类
 * @author li.zhang
 *
 */
@SuppressWarnings("serial")
public class CreateExecScheduleException extends Exception
{
    /**
     * 默认构造方法
     */
    public CreateExecScheduleException()
    {
        
    }
    
    /**
     * 构造方法
     * @param message
     */
    public CreateExecScheduleException(String message)
    {
        super(message);
    }
}
