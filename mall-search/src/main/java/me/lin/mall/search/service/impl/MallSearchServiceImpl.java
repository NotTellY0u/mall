package me.lin.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import me.lin.mall.common.to.es.SkuEsModel;
import me.lin.mall.common.utils.R;
import me.lin.mall.search.config.ElasticConfig;
import me.lin.mall.search.constant.EsConstant;
import me.lin.mall.search.feign.ProductFeignService;
import me.lin.mall.search.service.MallSearchService;
import me.lin.mall.search.vo.AttrResponseVo;
import me.lin.mall.search.vo.BrandVo;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Fibonacci
 * @create: 2021-03-02 16:55
 * @Version 1.0
 */
@Service("mallSearchService")
public class MallSearchServiceImpl implements MallSearchService {

    final
    ProductFeignService productFeignService;

    private final RestHighLevelClient client;

    public MallSearchServiceImpl(@Qualifier("esRestClient") RestHighLevelClient client, ProductFeignService productFeignService) {
        this.client = client;
        this.productFeignService = productFeignService;
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

            searchResult = buildSearchResult(response, param);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return searchResult;
    }

    /**
     * 构建结果数据
     *
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {

        SearchHits hits = response.getHits();

        SearchResult result = new SearchResult();
        // 1.返回的所有查询到的商品
        List<SkuEsModel> esModels = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (StringUtils.isNotBlank(param.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(string);
                }
                esModels.add(esModel);
            }
        }
        result.setProducts(esModels);
        // 2.当前所有商品涉及到的属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attrAgg = response.getAggregations().get("attr_agg");

        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            // 1.得到属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            // 2.得到属性的名字
            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            // 3.得到属性的所有值
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
                String keyAsString = item.getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());

            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);
        // 3.当前所有商品涉及到的菜单信息

        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            // 1.得到品牌id
            long brandId = bucket.getKeyAsNumber().longValue();
            // 2.得到品牌的名字
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            // 3.得到品牌的图片
            if (((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().size() > 0) {
                String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
                brandVo.setBrandImg(brandImg);
            }
            brandVo.setBrandId(brandId);
            brandVo.setBrandName(brandName);

            brandVos.add(brandVo);
        }

        result.setBrands(brandVos);
        // 4.当前所有商品涉及到的分类信息
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalogAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);
        // 5.分页信息-页码
        result.setPageNum(param.getPageNum());
        // 6.分页信息-总记录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        // 7.分页信息-总页码
        int totalPages = (int) total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) total / EsConstant.PRODUCT_PAGESIZE : (int) total / EsConstant.PRODUCT_PAGESIZE + 1;
        result.setTotalPages(totalPages);

        List<Integer> pageNavs = new ArrayList<>();

        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        // 8.构造面包屑导航功能
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
                // 1.分析每个attrs传过来的查询参数值
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo attrResponseVo = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(attrResponseVo.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }
                // 2. 取消这个面包屑以后，我们要跳转到哪个地方，将请求地址的URL替换掉
                String encode = replaceQueryString(param, attr,"attrs");
                navVo.setLink("http://search.linmall.com/list.html?" + encode);
                System.out.println(encode);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }

        if(param.getBrandId() != null && param.getBrandId().size() > 0){
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            R r = productFeignService.brandInfo(param.getBrandId());
            if(r.getCode() == 0){
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer stringBuffer = new StringBuffer();
                String  replace = null;
                for (BrandVo brandVo : brand) {
                    stringBuffer.append(brandVo.getBrandName()+";");
                    replace = replaceQueryString(param, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(stringBuffer.toString());
                navVo.setLink("http://search.linmall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }

        return result;
    }

    private String replaceQueryString(SearchParam param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+","%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replace = param.get_queryString().replace("&"+key+"=" + encode, "");
        return encode;
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
        if (param.getCatelog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catelogId", param.getCatelog3Id()));
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
        if (param.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }

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
        // 1.品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId").size(50);

        // 品牌聚合的子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brandAgg);

        // 分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catelogId").size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catelogName").size(10));
        sourceBuilder.aggregation(catalogAgg);

        // 属性聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        //  聚合出当前所有的attrId
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        // 聚合分析出当前attr_id对应的名字
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        // 聚合分析出当前attr_id对应的所有可能的属性值attrValue
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attrAgg.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(attrAgg);

        String s = sourceBuilder.toString();
        System.out.println("DSL的构建语句:" + s);

        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }
}
