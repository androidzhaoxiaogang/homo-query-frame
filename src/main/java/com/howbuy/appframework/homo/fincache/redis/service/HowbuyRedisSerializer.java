package com.howbuy.appframework.homo.fincache.redis.service;

import java.io.UnsupportedEncodingException;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class HowbuyRedisSerializer implements RedisSerializer {
	private JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();

	public byte[] serialize(Object t) throws SerializationException {
		if (t == null) {
			return new byte[] {};
		}
		byte type = 2;// 0 string 1 byte[] 2 other
		byte[] data = new byte[] {};
		if (t instanceof String) {
			type = 0;
			data = t.toString().getBytes();
		} else {
			if (t instanceof byte[]) {
				type = 1;
				data = (byte[]) t;
			} else {
				data = jdkSerializationRedisSerializer.serialize(t);
			}
		}
		byte[] rtnvalue = new byte[data.length + 1];
		rtnvalue[0] = type;
		System.arraycopy(data, 0, rtnvalue, 1, data.length);
		return rtnvalue;
	}

	public Object deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null) {
			return null;
		}
		byte type = bytes[0];// 0 string 1 byte[] 2 other
		byte[] data = new byte[bytes.length - 1];
		System.arraycopy(bytes, 1, data, 0, data.length);
		if (type == 0) {
			try {
				return new String(data, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (type == 1) {
			return data;
		}
		if (type == 2) {
			return data;
		}
		return jdkSerializationRedisSerializer.deserialize(data);
	}

}
