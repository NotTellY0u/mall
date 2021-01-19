package me.lin.mall.ware.vo;

import lombok.Data;

/**
 * @Author Fibonacci
 * @Date 2021/1/19 9:22 下午
 * @Version 1.0
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
