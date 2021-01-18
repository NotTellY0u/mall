package me.lin.mall.common.to;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Fibonacci
 */
@Data
public class MemberPrice {

    private Long id;

    private String name;

    private BigDecimal price;
}