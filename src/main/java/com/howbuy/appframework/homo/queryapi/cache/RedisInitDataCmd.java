package com.howbuy.appframework.homo.queryapi.cache;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.howbuy.appframework.homo.configure.commons.container.ContainerBootStrap;
import com.howbuy.appframework.homo.queryapi.parser.redis.RCCommander;

/**
 * 缓存刷新工具.
 * @author li.zhang
 *
 */
public class RedisInitDataCmd
{
    /** log **/
    private static final Logger logger = LogManager.getLogger(RedisInitDataCmd.class);
    
    /** redis缓冲处理指挥官. **/
    private RCCommander cacheCommander = new RCCommander();
    
    /**
     * 构造方法
     * @param commander
     */
    public RedisInitDataCmd()
    {
    }
    
    /**
     * 全量刷新.
     * @param args
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException
    {
        logger.info("Initialize container begin.......");
        //1.初始化.
        init();
        
        logger.info("Initialize container finished.....");
        
        Thread.sleep(3000);
        logger.info("======================================================================");
        
        logger.info("Begin refresh cache.........");
        
        //2.全量刷新缓存.
        refreshAll();
        
        logger.info("Refresh cache end sucessfully.........");
        
        logger.info("======================================================================");
    }

    /**
     * 初始化.
     */
    private static void init()
    {
        ContainerBootStrap bootStrap = new ContainerBootStrap();
        bootStrap.load();
    }
    
    /**
     * 全量刷新数据.
     */
    private static void refreshAll()
    {
        RedisInitDataCmd shell = new RedisInitDataCmd();
        
        //1.清空缓存.
        shell.cacheCommander.clearCache();
        
        //2.全量刷新
        shell.cacheCommander.refreshAllData();
    }
}
