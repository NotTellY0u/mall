package me.lin.mall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 核心原理
 * 1）.@EnableRedisHttpSession导入RedisHttpSessionConfiguration配置
 * 1.给容器中添加了一个组件
 * RedisOperationsSessionRepository:redis操作session：session增删改查的封装类
 * 2.SessionRepositoryFilter:session存储过滤器：每个请求过来必须经过filter
 * 1.创建的时候，就自动从容器中获取到了SessionRepository
 * 2.原生的request和response都被包装了。SessionRepositoryRequestWrapper
 * 3.以后获取Session使用 request.getSession()
 * 4.warpperRequest.getSession()
 */
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class MallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallAuthServerApplication.class, args);
    }

}
