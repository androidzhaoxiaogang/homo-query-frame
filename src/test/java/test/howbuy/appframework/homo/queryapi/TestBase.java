package test.howbuy.appframework.homo.queryapi;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Log4jConfigurer;

import com.howbuy.appframework.homo.configure.commons.container.ContainerBootStrap;

public abstract class TestBase extends TestCase
{
    /**
     * Spring控制器地址
     * 
     * @return
     * @author yeliang.wang
     * @date 2008-06-18
     */
    protected String[] getSpringContextPath()
    {
        String[] test = new String[1];
        test[0] = "applicationContext.xml";
        return test;

    }

    /**
     * 应用控制器对象声明
     * 
     * @return
     * @author yeliang.wang
     * @date 2008-06-18
     */
    protected ApplicationContext applicationContext = null;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp() 控制器加载
     */
    protected final void setUp() throws Exception
    {
        super.setUp();
        applicationContext = new ClassPathXmlApplicationContext(getSpringContextPath());

        initialize();

        Log4jConfigurer.initLogging("classpath:log4j.properties");

        ContainerBootStrap boots = new ContainerBootStrap();
        boots.load();
        boots.doRun();
    }

    /**
     * 初始化
     * 
     * @throws Exception
     */
    protected abstract void initialize() throws Exception;

}
