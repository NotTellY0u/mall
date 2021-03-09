package me.lin.mall.search.service.impl;

import me.lin.mall.search.config.ElasticConfig;
import me.lin.mall.search.constant.EsConstant;
import me.lin.mall.search.service.MallSearchService;
import me.lin.mall.search.vo.SearchParam;
import me.lin.mall.search.vo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
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
     * 查询
     *
     * @param param 检索的所有参数
     * @return
     */
    @Override
    public SearchResult search(SearchParam param) {
        // 1.动态构建出查询需要的DSL语句
        SearchResult searchResult = null;
        // 准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            // 2.执行检索请求
            SearchResponse response = client.search(searchRequest, ElasticConfig.COMMON_OPTIONS);

            Object result = buildSearchResult(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 构建结果数据
     *
     * @param response
     * @return
     */
    private Object buildSearchResult(SearchResponse response) {
        return null;
    }

    /**
     * 准备检索请求
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        // 构建DSL语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        /**
         * 模糊匹配、过滤（按照属性，分类，品牌，价格区间，库存）
         */
        // 1.组建bool - query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 1.1 must 模糊匹配
        if (StringUtils.isNotBlank(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        // 1.2 bool - filter 按照三级分类id查询
        if (param.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        // 1.2 bool - filter 按照品牌id查询
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        // 1.2 bool - filter 按照所有指定属性查询
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {

            for (String attrStr : param.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                //  每一个必须生成一个nested查询
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));

        if (StringUtils.isNotBlank(param.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if (s.length == 2) {
                // 区间
                rangeQuery.gte(s[0]).lte(s[1]);
            } else {
                if (param.getSkuPrice().startsWith("_")) {
                    rangeQuery.lte(s[0]);
                }
                if (param.getSkuPrice().endsWith("_")) {
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }

        sourceBuilder.query(boolQuery);

        /**
         * 排序，分页，高亮
         */
        // 2.1 排序
        if (StringUtils.isNotBlank(param.getSort())) {
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = "asc".equalsIgnoreCase(s[1]) ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], order);
        }
        // 2.2 分页 pageSize:5
        //pageNum:1 from:0 size:5
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        // 2.3 高亮
        if (StringUtils.isNotBlank(param.getKeyword())) {
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }
        /**
         * 聚合分析
         */
        String s = sourceBuilder.toString();
        System.out.println("DSL的构建语句:" + s);

        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }
}
