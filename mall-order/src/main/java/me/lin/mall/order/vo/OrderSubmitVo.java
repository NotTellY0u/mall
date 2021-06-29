package me.lin.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交数据
 * @author Fibonacci
 */
@Data
public class OrderSubmitVo {
    private Long addrId;
    private Integer payType;

    //无需提交需要购买的商品，去购物车再获取一遍

    //防重令牌
    private String orderToken;

    //应付价格 验价
    private BigDecimal payPrice;

    private String note;
}
