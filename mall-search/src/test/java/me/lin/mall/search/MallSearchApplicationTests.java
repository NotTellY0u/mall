package me.lin.mall.search;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.ToString;
import me.lin.mall.search.config.ElasticConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class MallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @ToString
    @Data
    static class Account{
        int account_number;
        int balance;
        String firstname;
        String lastname;
        int age;
        String gender;
        String address;
        String employer;
        String email;
        String city;
    }

    @Test
    public void seearchRequest() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));

        TermsAggregationBuilder aggAge = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(aggAge);

        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, ElasticConfig.COMMON_OPTIONS);

        System.out.println("检索条件"+searchSourceBuilder.toString());
        System.out.println(search);
        SearchHits hits = search.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String sourceAsString = searchHit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("账号信息:"+account);
        }
        Aggregations aggregations = search.getAggregations();
/*        for (Aggregation aggregation : aggregations.asList()) {
            System.out.println("当前聚合" + aggregation.getName());
            aggregations.get()
        }*/
        Terms ageAgg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄："+keyAsString);
        }

        Avg avg = aggregations.get("balanceAvg");
        System.out.println("平均薪资："+avg.getValue());
    }

    @Test
    public void indexData() throws IOException {
        IndexRequest users = new IndexRequest("users");
        users.id("1");
        User user = new User();
        user.setUserName("张三");
        user.setAge(18);
        user.setGender("男");
        String s = JSON.toJSONString(user);
        users.source(s, XContentType.JSON);
        IndexResponse index = client.index(users, ElasticConfig.COMMON_OPTIONS);
    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }

    @Test
    void contextLoads() {
    }



}
