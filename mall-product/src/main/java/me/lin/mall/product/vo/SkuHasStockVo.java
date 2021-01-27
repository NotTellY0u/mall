package me.lin.mall.product.vo;

import lombok.Data;

/**
 * @Author Fibonacci
 * @create: 2021-01-27 13:53
 * @Version 1.0
 */
@Data
public class SkuHasStockVo {
    private Long skuId;

    private Boolean hasStock;
}
