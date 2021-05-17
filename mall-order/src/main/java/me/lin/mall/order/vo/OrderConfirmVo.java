package me.lin.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页需要的数据
 *
 * @Author Fibonacci
 * @Date 2021/5/16 5:08 下午
 * @Version 1.0
 */
@Data
public class OrderConfirmVo {

    /**
     * 收货地址
     */
    List<MemberAddressVo> address;

    /**
     * 所有选中的购物项
     */
    List<OrderItemVo> items;

    /**
     * 发票信息
     */

    /**
     * 优惠券信息
     */
    private Integer integration;

    /**
     * 订单总额
     */
    private BigDecimal total;

    /**
     * 应付总额
     */
    private BigDecimal payPrice;
}
