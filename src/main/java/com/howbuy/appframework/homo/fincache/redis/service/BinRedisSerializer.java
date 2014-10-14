package com.howbuy.appframework.homo.fincache.redis.service;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class BinRedisSerializer implements RedisSerializer<byte[]> {

	public byte[] serialize(byte[] t) throws SerializationException {
		return t;
	}

	public byte[] deserialize(byte[] bytes) throws SerializationException {
		return bytes;
	}

}
