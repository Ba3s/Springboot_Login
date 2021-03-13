package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import redis.clients.jedis.Jedis;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        RedisTemplate<String, String> stringTemplate = new RedisTemplate<>();
        GsonJsonParser gsonJsonParser = new GsonJsonParser();


    }

}
