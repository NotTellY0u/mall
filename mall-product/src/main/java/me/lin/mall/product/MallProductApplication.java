package me.lin.mall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
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
 * 				2）.
 * 			2.配置Mybatis-Plus
 */
@SpringBootApplication
public class MallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallProductApplication.class, args);
	}

}
