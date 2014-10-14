package com.howbuy.appframework.homo.configure.commons.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.PropertyConfigurator;

@SuppressWarnings("serial")
public class Log4jInitServlet extends HttpServlet
{
    public Log4jInitServlet()
    {
        
    }

    public void init(ServletConfig config) throws ServletException
    {
        String prefix = config.getServletContext().getRealPath("/");
        StringBuilder logFile = new StringBuilder();
        Properties props = new Properties();
        InputStream istream = null;
        try
        {
            istream =  Log4jInitServlet.class.getClassLoader().getResourceAsStream("log4j.properties");
            props.load(istream);
            logFile.append(prefix).append(props.getProperty("log4j.appender.R.File"));
            props.setProperty("log4j.appender.R.File", logFile.toString());
            
            //装入log4j配置信息
            PropertyConfigurator.configure(props);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (istream != null)
            {
                try
                {
                    istream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
    }
}
