package test.howbuy.appframework.homo.fincache.redis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.data.redis.support.collections.DefaultRedisZSet;
import org.springframework.data.redis.support.collections.RedisZSet;

import com.howbuy.appframework.homo.fincache.redis.service.RedisClientService;

public class TestQuery
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        String[] writeservers = new String[] {"192.168.220.105:6579:howbuy"};//redisHost:redisPort:password
        //String[] readservers = new String[] { "10.11.8.245:6379" };
        RedisClientService<String, String> writeService = new RedisClientService<String, String>(String.class, 
                String.class, writeservers);
        RedisClientService<String, String> readService = writeService;
        
        testRedisSetCmd(writeService);
        
        testRedisMapCmd(writeService);
        
        //下面是从redis中做的查询.
        int userid = 3;
        SortQuery<String> query = SortQueryBuilder.sort("user_" + userid)
                .noSort().get("#").get("strategy-map-*->strategy_id")
                .get("strategy-map-*->strategy_name")
                .get("strategy-map-*->strategy_content").build();
        List<String> result = readService.getRedisTemplate().sort(query);
        System.out.println(result);
    }

    /**
     * 测试redis set相关的命令
     * @param writeService
     */
    private static void testRedisSetCmd(RedisClientService<String, String> writeService)
    {
        int size = 10;
        int strategyid_offset = 100;
        for (int i = 1; i <= size; i++)
        {
            RedisZSet<String> set = new DefaultRedisZSet<String>("user_" + i, writeService.getRedisTemplate());
            set.clear();
            for (int j = 0; j < 5; j++)
            {
                int id = i * strategyid_offset + j;
                set.add(String.valueOf(id));
            }
        }
    }
    
    /**
     * 测试redis map相关的命令.
     * @param writeService
     */
    private static void testRedisMapCmd(RedisClientService<String, String> writeService)
    {
        Set<Integer> strategySet = new HashSet<Integer>();
        for (int strategyid : strategySet)
        {
            DefaultRedisMap<String, String> map = new DefaultRedisMap<String, String>("strategy-map-" + strategyid,
                    writeService.getRedisTemplate());
            map.clear();
            map.put("strategy_id", String.valueOf(strategyid));
            map.put("strategy_name", "nameValue_" + strategyid);
            map.put("strategy_content", "contentValue_" + strategyid);
        }
    }
}
