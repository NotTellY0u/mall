package me.lin.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-03-02 16:53
 * @Version 1.0
 */
@Data
public class SearchParam {

    /**
     *   页面传来的全文匹配关键字
     */
    private String keyword;

    /**
     *   三级分类id
     */
    private Long catelog3Id;
    /**
     *  排序条件
     *  saleCount_asc/desc
     *  skuPrice_asc/desc
     *  hostScore_asc/desc
     */
    private String sort;
    /**
     * 过滤条件
     * hasStock(有货)\skuPrick区间\brandId\catalog3Id\attrs\
     * hasStock=0\1
     * skuPrice=1_500\500_1000
     */
    private Integer hasStock;
    private String skuPrice;
    private List<Long> brandId;
    private List<String> attrs;
    private Integer pageNum = 1;
    private String _queryString;
}
