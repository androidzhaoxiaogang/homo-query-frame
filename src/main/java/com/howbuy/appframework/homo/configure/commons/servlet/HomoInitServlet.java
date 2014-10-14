package com.howbuy.appframework.homo.configure.commons.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.howbuy.appframework.homo.configure.commons.container.ContainerBootStrap;

@SuppressWarnings("serial")
public class HomoInitServlet extends HttpServlet
{
    /** 是否已经初始化. **/
    private boolean inited;
    
    /** 容器启动类. **/
    private ContainerBootStrap bootStrap;
    
    /**
     * 初始化.
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        
        //启动容器.
        startContainer();
    }

    /**
     * 启动容器.
     */
    private void startContainer()
    {
        if (!inited)
        {
            bootStrap = new ContainerBootStrap();
            bootStrap.load();
            bootStrap.doRun();
            
            inited = true;
        }
    }
}
