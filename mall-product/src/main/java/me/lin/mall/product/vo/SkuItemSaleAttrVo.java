package me.lin.mall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author Fibonacci
 * @Date 2021/3/23 9:46 上午
 * @Version 1.0
 */
@ToString
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
