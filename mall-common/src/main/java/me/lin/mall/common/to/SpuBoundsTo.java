package me.lin.mall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Fibonacci
 * @create: 2021-01-18 16:58
 * @Version 1.0
 */
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
