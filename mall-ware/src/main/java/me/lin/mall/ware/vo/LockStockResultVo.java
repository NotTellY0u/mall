package me.lin.mall.ware.vo;

import lombok.Data;

/**
 * @author Fibonacci
 */
@Data
public class LockStockResultVo {

    private Long skuId;

    private Integer num;

    private Boolean locked;
}
