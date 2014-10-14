package com.howbuy.appframework.homo.configure.commons.container;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerService
{

    private static String logFile = "log4j.properties";
    private static Logger logger = Logger.getLogger(LoggerService.class);

    /**
     * Constructor for LoggerService.
     */
    public LoggerService()
    {
        super();
    }

    public static void load()
    {
        String temp = LoggerService.class.getClassLoader().getResource(logFile).getPath();
        PropertyConfigurator.configure(temp);
    }

}
