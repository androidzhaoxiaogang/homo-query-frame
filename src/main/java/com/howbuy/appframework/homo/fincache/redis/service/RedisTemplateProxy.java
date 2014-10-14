package com.howbuy.appframework.homo.fincache.redis.service;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisTemplateProxy<K, V> {
	private RedisTemplate<K, V> redisTemplate;

	public RedisTemplate<K, V> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<K, V> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	private boolean valid = true;

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
