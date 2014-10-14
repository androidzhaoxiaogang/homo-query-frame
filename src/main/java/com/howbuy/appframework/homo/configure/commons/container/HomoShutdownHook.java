package com.howbuy.appframework.homo.configure.commons.container;

/**
 * 系统退出时的钩子线程.
 * @author li.zhang
 *
 */
public class HomoShutdownHook extends Thread
{
    /**
     * 构造方法
     * @param name 线程名称
     */
    public HomoShutdownHook(String name)
    {
        super(name);
    }
    
    public void run()
    {
        //TODO 
    }
}
