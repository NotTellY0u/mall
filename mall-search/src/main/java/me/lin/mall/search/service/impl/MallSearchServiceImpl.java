package me.lin.mall.search.service.impl;

import me.lin.mall.search.service.MallSearchService;
import me.lin.mall.search.vo.SearchParam;
import me.lin.mall.search.vo.SearchResult;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Author Fibonacci
 * @create: 2021-03-02 16:55
 * @Version 1.0
 */
@Service("mallSearchService")
public class MallSearchServiceImpl implements MallSearchService {

    private final RestHighLevelClient client;

    public MallSearchServiceImpl(@Qualifier("esRestClient") RestHighLevelClient client) {
        this.client = client;
    }

    /**
     *  查询
     * @param param 检索的所有参数
     * @return
     */
    @Override
    public SearchResult search(SearchParam param) {
        SearchRequest searchRequest = new SearchRequest();

        return null;
    }
}
