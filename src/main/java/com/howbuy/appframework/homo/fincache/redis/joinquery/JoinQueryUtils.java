package com.howbuy.appframework.homo.fincache.redis.joinquery;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.query.SortCriterion;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.data.redis.support.collections.DefaultRedisZSet;
import org.springframework.data.redis.support.collections.RedisZSet;

import com.howbuy.appframework.homo.fincache.redis.service.RedisClientService;

public class JoinQueryUtils
{
    /**
     * 写入主表对象.
     * 
     * @param writeService 写服务
     * @param priEntity 主实体对象
     */
    public static void writePriEntity(RedisClientService<String, String> writeService, PriEntity priEntity)
    {
        StringBuilder key = new StringBuilder();
        key.append(priEntity.getnamespace()).append("-pri-").append(priEntity.getKey());
        RedisZSet<String> set = new DefaultRedisZSet<String>(key.toString(), writeService.getRedisTemplate());
        set.add(priEntity.getValue());
    }

    /**
     * 写入关联表对象.
     * 
     * @param writeService 写服务
     * @param priEntity 主实体对象
     * @param subEntity 关联表对象
     */
    public static void writeSubEntity(RedisClientService<String, String> writeService, PriEntity priEntity, SubEntity subEntity)
    {
        StringBuilder key = new StringBuilder();
        key.append(priEntity.getnamespace())
               .append("-subentity-")
               .append(subEntity.getnamespace())
               .append("-").append(subEntity.getKey());
        
        DefaultRedisMap<String, String> map = new DefaultRedisMap<String, String>(key.toString(), writeService.getRedisTemplate());
        map.putAll(subEntity.getValueMap());
    }

    /**
     * 查询.
     * 
     * @param readService
     *            读服务
     * @param priEntity
     *            主对象实例
     * @param subEntitynamespace
     *            关联表namespace
     * @param fields
     *            查询字段
     * @return 查询结果集合 list<Map<查询字段,值>>
     */
    public static List<Map<String, String>> query(
            RedisClientService<String, String> readService,
            PriEntity priEntity, String subEntitynamespace, String[] fields)
    {
        List<Map<String, String>> rtnValue = new ArrayList<Map<String, String>>();
        String prefix = priEntity.getnamespace() + "-subentity-"
                + subEntitynamespace + "-*";
        SortCriterion<String> sortCriterion = SortQueryBuilder
                .sort(priEntity.getnamespace() + "-pri-" + priEntity.getKey())
                .noSort().get("#");
        for (String field : fields)
        {
            sortCriterion = sortCriterion.get(prefix + "->" + field);
        }
        SortQuery<String> query = sortCriterion.build();
        List<String> result = readService.getRedisTemplate().sort(query);
        for (int i = 0; i < result.size() / (1 + fields.length); i++)
        {
            Map<String, String> values = new LinkedHashMap<String, String>();
            for (int j = 1; j <= fields.length; j++)
            {
                String data = result.get(i * (1 + fields.length) + j);
                values.put(fields[j - 1], data);
            }
            rtnValue.add(values);
        }
        return rtnValue;

    }
}