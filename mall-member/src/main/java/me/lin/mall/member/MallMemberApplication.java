package me.lin.mall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author Fibonacci
 * 1.想要远程调用别的服务
 * 	1).引入openfeign
 * 	2).编写一个接口，告诉SpringCloud这个接口需要调用远程服务
 * 		1.声明接口的每一个方法都是调用该远程服务的指定请求
 *  3).开启远程调用功能
 */
@EnableFeignClients(basePackages = "me.lin.mall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class MallMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallMemberApplication.class, args);
	}

}
