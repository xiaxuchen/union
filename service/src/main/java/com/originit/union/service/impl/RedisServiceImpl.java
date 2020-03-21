package com.originit.union.service.impl;

import com.alibaba.fastjson.JSON;
import com.originit.union.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, Object value) {
        if (value instanceof String || value instanceof Integer || value instanceof Long) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else {
            stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value));
        }

    }

    @Override
    public <T> T get(String key,Class<T> type) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (type == String.class) {
            return (T) value;
        }
        if (type == Integer.class) {
            return (T)((Integer)Integer.parseInt(value));
        }
        if (type == Long.class) {
            return (T)((Long)Long.parseLong(value));
        }

        return JSON.parseObject(value,type);
    }

    @Override
    public boolean expire(String key, long expire) {
        return stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public void remove(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key,delta);
    }
}