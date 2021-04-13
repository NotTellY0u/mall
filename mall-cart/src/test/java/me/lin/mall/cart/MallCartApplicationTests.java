package me.lin.mall.cart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class MallCartApplicationTests {
    final StringRedisTemplate redisTemplate;

    public MallCartApplicationTests(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("1", "1");
    }

}
