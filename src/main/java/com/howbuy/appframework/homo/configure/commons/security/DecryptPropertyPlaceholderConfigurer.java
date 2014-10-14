package com.howbuy.appframework.homo.configure.commons.security;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.howbuy.appframework.homo.queryapi.common.HomoProperty;

/**
 * 解密配置文件中的加密串.
 * @author li.zhang
 *
 */
public class DecryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer
{
    
    /**
     * 对加密的属性解密.
     */
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
        throws BeansException
    {
        String passwd = props.getProperty(HomoProperty.MONITOR_DB_PASSWORD);
        if (passwd != null)
        {
            props.setProperty(HomoProperty.MONITOR_DB_PASSWORD, "");
        }
        
        //解密后，按照父类的逻辑处理..
        super.processProperties(beanFactory, props);
    }
}
