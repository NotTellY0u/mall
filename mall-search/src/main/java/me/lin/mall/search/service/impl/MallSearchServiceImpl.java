package me.lin.mall.search.service.impl;

import me.lin.mall.search.service.MallSearchService;
import me.lin.mall.search.vo.SearchParam;
import me.lin.mall.search.vo.SearchResult;
import org.springframework.stereotype.Service;

/**
 * @Author Fibonacci
 * @create: 2021-03-02 16:55
 * @Version 1.0
 */
@Service("mallSearchService")
public class MallSearchServiceImpl implements MallSearchService {

    /**
     *  查询
     * @param param 检索的所有参数
     * @return
     */
    @Override
    public SearchResult search(SearchParam param) {

        return null;
    }
}
