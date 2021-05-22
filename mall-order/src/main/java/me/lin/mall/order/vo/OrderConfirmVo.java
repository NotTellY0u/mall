package me.lin.mall.order.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页需要的数据
 *
 * @Author Fibonacci
 * @Date 2021/5/16 5:08 下午
 * @Version 1.0
 */
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
     * 防重令牌
     */
    private String orderToken;
    /**
     * 订单总额
     */
//    private BigDecimal total;

    /**
     * 应付总额
     */
//    private BigDecimal payPrice;
    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);

            }

        }
        return sum;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }

    public List<MemberAddressVo> getAddress() {
        return address;
    }

    public void setAddress(List<MemberAddressVo> address) {
        this.address = address;
    }

    public List<OrderItemVo> getItems() {
        return items;
    }

    public void setItems(List<OrderItemVo> items) {
        this.items = items;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }
}
