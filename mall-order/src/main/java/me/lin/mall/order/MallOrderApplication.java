package me.lin.mall.order;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 使用rabbitMQ
 * 1. 引入amqp场景：RabbitAutoConfiguration就会自动生效
 * 2. 给容器中自动配置了
 * RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessageTemplate
 * 所有属性都是 spring.rabbitmq
 * @configurationProerties(prefix = "spring.rabbitmq")
 * public class RabbitProperties
 * 3.给配置文件中配置spring.rabbitmq信息
 * 4. @EnableRabbit：@EnableXXX：开启功能
 * 5.监听消息：使用@RabbitListener：必须有@EnableRabbit
 * @RabbitListener：类+方法上（监听哪些队列即可）
 * @RabbitHandler：标在方法上（重载区分不同的消息）
 *
 * 本地事务失效问题
 * 同一个对象内事务方法互调默认失效，原因是绕过了代理，事务使用代理对象来控制的
 *  1）、引入aop-starter;spring-boot-starter-aop;引入了aspectJ
 *  2）、@EnableAspectJAutoProxy:开启aspectJ动态代理。以后所有的动态代理都是AspectJ创建的（即使没有接口也可以创建动态代理），对外暴露代理对象
 *  3）、本类互调用调用对象
 *
 *  Seata控制分布式事务
 *  1）、每一个微服务必须先创建undo_log
 *  2）、安装事务协调器
 *
 */
//@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients
//@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class MallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallOrderApplication.class, args);
	}

}
