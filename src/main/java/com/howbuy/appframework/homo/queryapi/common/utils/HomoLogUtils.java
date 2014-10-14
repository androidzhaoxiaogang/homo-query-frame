package com.howbuy.appframework.homo.queryapi.common.utils;

import java.io.PrintStream;

import org.apache.log4j.Logger;
import com.howbuy.appframework.homo.queryapi.common.HomoConstants;
import com.howbuy.appframework.homo.queryapi.common.struct.Condition;

public class HomoLogUtils
{
    /**
     * 打印出异常堆栈
     * @param out
     * @param e
     */
    public static void printStackTrace(PrintStream out, Throwable e)
    {
        StringBuilder message = new StringBuilder();
        message.append(e.toString()).append(System.getProperty(HomoConstants.LINE_SEPARATOR));
        
        StackTraceElement[] trace = e.getStackTrace();
        for (int i = 0; i < trace.length; i++)
        {
            message.append("\tat " + trace[i]).append(System.getProperty(HomoConstants.LINE_SEPARATOR));
        }
        
        synchronized (out)
        {
            out.println(message.toString());
        }
    }
    
    /**
     * 打印出异常堆栈
     * @param log 
     * @param e
     */
    public static void printStackTrace(Logger logger, Throwable e)
    {
        StringBuilder message = new StringBuilder();
        message.append(e.toString()).append(System.getProperty(HomoConstants.LINE_SEPARATOR));
        
        StackTraceElement[] trace = e.getStackTrace();
        for (int i = 0; i < trace.length; i++)
        {
            message.append("\tat " + trace[i]).append(System.getProperty(HomoConstants.LINE_SEPARATOR));
        }
        
        synchronized (logger)
        {
            logger.error(message.toString());
        }
    }
    
    public static void main(String[] args)
    {
        try
        {
            contructException();
        }
        catch(Throwable e)
        {
            HomoLogUtils.printStackTrace(System.out, e);
        }
        
    }

    private static void contructException()
    {
        Condition s = new Condition();
        String command = s.getCommand();
        command.length();
    }
}
