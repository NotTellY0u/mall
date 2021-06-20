package me.lin.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Fibonacci
 * @create: 2021-01-25 14:05
 * @Version 1.0
 * 1.导入配置
 * 2.编写配置，给容器中注入一个RestHighLevelClient
 */

@Configuration
public class ElasticConfig {

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient(){

        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.77.177",9200,"http"));

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }
}
