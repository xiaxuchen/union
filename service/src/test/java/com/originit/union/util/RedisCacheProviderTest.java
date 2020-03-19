package com.originit.union.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisCacheProviderTest {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Test
    public void test() {
        redisTemplate.opsForList().set("xxccc",1,"hahah");
    }
}
