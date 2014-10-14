package test.howbuy.appframework.homo.fincache.redis;

import java.util.HashMap;
import java.util.Map;

import com.howbuy.appframework.homo.fincache.redis.joinquery.SubEntity;

public class StrategyEntity implements SubEntity
{
    private long id;
    private String name;
    private String content;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getnamespace()
    {
        return "Strategy";
    }

    public String getKey()
    {
        return id + "";
    }

    public Map<String, String> getValueMap()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id + "");
        map.put("name", name);
        map.put("content", content);
        return map;
    }

}
