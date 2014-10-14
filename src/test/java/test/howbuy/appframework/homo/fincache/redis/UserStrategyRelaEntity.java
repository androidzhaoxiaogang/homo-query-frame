package test.howbuy.appframework.homo.fincache.redis;

import com.howbuy.appframework.homo.fincache.redis.joinquery.PriEntity;

public class UserStrategyRelaEntity implements PriEntity
{
    private long userid;
    private long strategyid;

    public long getUserid()
    {
        return userid;
    }

    public void setUserid(long userid)
    {
        this.userid = userid;
    }

    public long getStrategyid()
    {
        return strategyid;
    }

    public void setStrategyid(long strategyid)
    {
        this.strategyid = strategyid;
    }

    public String getnamespace()
    {
        return "UserStrategy";
    }

    public String getKey()
    {
        return userid + "";
    }

    public String getValue()
    {
        return strategyid + "";
    }

}
