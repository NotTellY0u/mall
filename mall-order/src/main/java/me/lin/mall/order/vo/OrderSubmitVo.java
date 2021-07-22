package me.lin.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交数据
 * @author Fibonacci
 */
@Data
public class OrderSubmitVo {
    /**
     * 收货地址
     */
    private Long addrId;
    /**
     * 支付方式
     */
    private Integer payType;

    //无需提交需要购买的商品，去购物车再获取一遍

    //防重令牌
    private String orderToken;

    //应付价格 验价
    private BigDecimal payPrice;

    /**
     * 订单备注
     */
    private String note;
}
