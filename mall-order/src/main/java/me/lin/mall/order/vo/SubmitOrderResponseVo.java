package me.lin.mall.order.vo;

import lombok.Data;
import me.lin.mall.order.entity.OrderEntity;

@Data
public class SubmitOrderResponseVo {
    /**
     * 订单实体类
     */
    private OrderEntity orderEntity;

    /**
     * 错误状态码 0：成功
     */
    private Integer code;
}
