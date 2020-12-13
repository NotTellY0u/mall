package me.lin.mall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Fibonacci
 * 1.如何使用nacos作为配置中心统一管理配置
 *  1).引入依赖
 *         <dependency>
 *             <groupId>com.alibaba.cloud</groupId>
 *             <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 *             <version>2.2.0.RELEASE</version>
 *         </dependency>
 *   2).创建一个bootstrap.properties
 *   配置
 *   spring.application.name=mall-coupon
 *   spring.cloud.config.server-addr=127.0.0.1:8848
 *   3).需要给配置中心默认添加一个叫数据集（Data ID)mall-coupon.properties (默认命名规则：应用名.proerties)
 *   4).在应用名.proerties中添加任何配置
 *   5).动态获取配置
 *     @RefreshScope:动态获取并刷新配置
 *     @Value("${配置项名}"):获取到配置
 *     如果配置中心和当前应用的配置文件中都配置了相同的项，优先使用配置中心的配置
 *  2.细节
 *   1).命名空间(配置隔离):
 *      默认：public(保留空间)：默认新增的所有配置都在public空间
 *      1.开发、测试、生产：利用命名空间做环境隔离。
 *      注意：在bootstrap.properties:配置上，需要使用哪个命名空间下的配置，
 *      加上spring.cloud.nacos.config.namespace=对应的命名空间唯一id
 *      2.每一个微服务之间互相隔离配置，每一个微服务都创建自己的命名空间，只加载自己命名空间下的所有配置
 *   2).配置集:所有的配置的集合
 *   3).配置集ID:类似文件名
 *   4).配置分组：
 *   默认所有配置集都属于：DEFAULT_GROUP:
 *   项目中的使用：每个微服务创建自己的命名空间，使用配置分组区分环境
 *
 *  3.同时加载多个配置集
 *   1).微服务任何配置信息，任何配置文件都可以放在配置中心中
 *   2).只需要在bootstrap.properties说明加载配置中心那些配置文件即可
 *   3).@value、@ConfigurationProperties都可以从配置文件中取值，优先从配置中心取值
 */
@EnableDiscoveryClient
@SpringBootApplication
public class MallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallCouponApplication.class, args);
    }

}
