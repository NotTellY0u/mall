package me.lin.mall.search.vo;

import lombok.Data;
import me.lin.mall.common.to.es.SkuEsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Fibonacci
 * @Date 2021/3/2 9:07 下午
 * @Version 1.0
 */
@Data
public class SearchResult {
    /**
     * 面包屑导航数据
     */
    private List<NavVo> navs = new ArrayList<>();
    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String  link;
    }
    /**
     * 查询到的所有商品信息
     */
    private List<SkuEsModel> products;

    /**
     * 当前页面
     */
    private Integer pageNum;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页码
     */
    private Integer totalPages;

    /**
     * 当前查询到的结果所有涉及到的品牌
     */
    private List<BrandVo> brands;


    private List<CatalogVo> catalogs;
    /**
     * 当前查询到的结果所有涉及到的属性
     */
    private List<AttrVo> attrs;
    /**
     * 导航页码
     */
    private List<Integer> pageNavs;

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
