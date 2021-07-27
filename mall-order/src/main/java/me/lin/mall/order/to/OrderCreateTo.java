package me.lin.mall.order.to;

import lombok.Data;
import me.lin.mall.order.entity.OrderEntity;
import me.lin.mall.order.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 小镜子
 */
@Data
public class OrderCreateTo {

    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    /**
     * 订单计算的应付价格
     */
    private BigDecimal payPrice;

    /**
     * 运费
     */
    private BigDecimal fare;
}
