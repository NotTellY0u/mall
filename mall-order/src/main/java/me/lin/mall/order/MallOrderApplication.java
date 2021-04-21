package me.lin.mall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * 使用rabbitMQ
 * 1. 引入amqp场景：RabbitAutoConfiguration就会自动生效
 * 2. 给容器中自动配置了
 * RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessageTemplate
 * 3. @EnableRabbit
 */
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class MallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallOrderApplication.class, args);
	}

}
