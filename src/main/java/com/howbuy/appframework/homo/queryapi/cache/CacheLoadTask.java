package com.howbuy.appframework.homo.queryapi.cache;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CacheLoadTask extends Thread
{
    private List<CacheLoader> loaders;
    private int duringSecond = 60;
    private static AtomicBoolean cacheloaded = new AtomicBoolean(false);

    public static boolean isCacheloaded()
    {
        return cacheloaded.get();
    }

    /**
     * @return the duringSecond
     */
    public int getDuringSecond()
    {
        return duringSecond;
    }

    /**
     * @param duringSecond
     *            the duringSecond to set
     */
    public void setDuringSecond(int duringSecond)
    {
        this.duringSecond = duringSecond;
    }

    @Override
    public void run()
    {
        this.setName("CacheLoadTask");
        int count = 0;
        // DataSourceManager manager = new DataSourceManager();
        while (true)
        {
            if (count % 100 == 0)
            {
                // try {
                // manager.initDataSource();
                // } catch (Exception ex) {
                // ex.printStackTrace();
                // }
                count = 0;
            }
            count++;
            if (count > 1000)
            {
                count = 0;
            }
            try
            {
                for (CacheLoader loader : loaders)
                {
                    try
                    {
                        loader.load2cache();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            cacheloaded.set(true);
            try
            {
                Thread.sleep(duringSecond * 1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public List<CacheLoader> getLoaders()
    {
        return loaders;
    }

    public void setLoaders(List<CacheLoader> loaders)
    {
        this.loaders = loaders;
    }
}
