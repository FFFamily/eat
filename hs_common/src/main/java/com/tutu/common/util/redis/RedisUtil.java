package com.tutu.common.util.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisUtil {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void putCache(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public void putCache(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    public String getCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void removeCache(String key) {
        redisTemplate.delete(key);
    }
}
