package test.howbuy.appframework.homo.fincache.redis;

import java.util.List;
import java.util.Map;

import com.howbuy.appframework.homo.fincache.redis.joinquery.JoinQueryUtils;
import com.howbuy.appframework.homo.fincache.redis.service.RedisClientService;

public class TestJoinQuery
{
    public static void main(String[] args)
    {
        String[] writeservers = new String[] { "192.168.220.105:6579:howbuy" };//redisHost:redisPort:password
        RedisClientService<String, String> writeService = new RedisClientService<String, String>(String.class, 
                String.class, writeservers);
        RedisClientService<String, String> readService = writeService;
        
        /** 向redis中存储 **/
        store2Redis(writeService);

        /** 从redis中查询 **/
        queryFromRedis(readService);
    }

    private static void store2Redis(RedisClientService<String, String> writeService)
    {
        int size = 10;
        for (int i = 1; i <= size; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                int strategyid = i * 100 + j;
                UserStrategyRelaEntity userStrategyRelaEntity = new UserStrategyRelaEntity();
                userStrategyRelaEntity.setUserid(i);
                userStrategyRelaEntity.setStrategyid(strategyid);
                JoinQueryUtils.writePriEntity(writeService, userStrategyRelaEntity);

                StrategyEntity strategyEntity = new StrategyEntity();
                strategyEntity.setId(strategyid);
                strategyEntity.setName("strategyName_" + strategyid);
                strategyEntity.setContent("strategyContent_" + strategyid);
                JoinQueryUtils.writeSubEntity(writeService, userStrategyRelaEntity, strategyEntity);
            }
        }
    }
    
    private static void queryFromRedis(RedisClientService<String, String> readService)
    {
        UserStrategyRelaEntity userStrategyRelaEntity = new UserStrategyRelaEntity();
        userStrategyRelaEntity.setUserid(1);
        StrategyEntity strategyEntity = new StrategyEntity();
        List<Map<String, String>> findValueList = JoinQueryUtils.query(readService, 
                userStrategyRelaEntity,
                strategyEntity.getnamespace(), 
                new String[] { "id", "name", "content" });
        System.out.println(findValueList);
    }

}
