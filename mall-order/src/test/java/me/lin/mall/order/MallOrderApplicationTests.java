package me.lin.mall.order;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@SpringBootTest
class MallOrderApplicationTests {

    @Test
    void contextLoads() {
    }

}
