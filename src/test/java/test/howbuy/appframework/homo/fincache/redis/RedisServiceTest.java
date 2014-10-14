package test.howbuy.appframework.homo.fincache.redis;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.collections.DefaultRedisList;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.data.redis.support.collections.DefaultRedisSet;
import org.springframework.data.redis.support.collections.DefaultRedisZSet;
import org.springframework.data.redis.support.collections.RedisList;
import org.springframework.data.redis.support.collections.RedisMap;
import org.springframework.data.redis.support.collections.RedisSet;
import org.springframework.data.redis.support.collections.RedisZSet;

import com.howbuy.appframework.homo.fincache.redis.service.RedisClientService;

public class RedisServiceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] writeservers = new String[] { "192.168.220.105:6579" };
		String[] readservers = new String[] { "192.168.220.105:6479"};
		RedisClientService<String, String> writeService = new RedisClientService<String, String>(
				String.class, String.class, writeservers);
		RedisClientService<String, String> readService = new RedisClientService<String, String>(
				String.class, String.class, readservers);
		ValueOperations<String, String> voper = writeService.getRedisTemplate().opsForValue();
		voper.set("f", "jxaaaaaaaaaa");
		ValueOperations<String, String> voper_read = readService
				.getRedisTemplate().opsForValue();
		String data = voper_read.get("f");
		System.out.println(data);
		RedisList<String> list = new DefaultRedisList<String>("listCacheKey",
				writeService.getRedisTemplate());
		
		RedisMap<String, String> map = new DefaultRedisMap<String, String>(
				"mapCacheKey", writeService.getRedisTemplate());
		RedisZSet<String> zset = new DefaultRedisZSet<String>("zsetCacheKey",
				writeService.getRedisTemplate());
		RedisSet<String> set = new DefaultRedisSet<String>("setCacheKey",
				writeService.getRedisTemplate());
	}
}
