package me.lin.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *  * @author Fibonacci
 *
 * 1.整合Mybatis-Plus
 * 		1）.导入依赖
 *        <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.3.1</version>
 *         </dependency>
 * 		2）.配置
 * 			1.配置数据源
 * 				1）.导入数据库的驱动
 * 				2）.在application.yml配置数据源相关信息
 * 			2.配置Mybatis-Plus
 * 				1）.使用@MapperScan
 * 				2）.告诉Mybatis-Plus,sql映射文件位置
 * 	2.使用逻辑删除
 * 		1).配置全局的逻辑删除规则（省略）
 * 		2).配置逻辑删除的组件Bean（省略）
 * 		3).给Bean加上逻辑删除注解@TableLogic
 */

@MapperScan("me.lin.mall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class MallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallProductApplication.class, args);
	}

}
