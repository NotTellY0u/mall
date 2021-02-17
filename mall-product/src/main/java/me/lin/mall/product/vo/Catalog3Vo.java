package me.lin.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Fibonacci
 * @Date 2021/2/17 4:23 下午
 * @Version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catalog3Vo {

    private String id;

    private String name;

    private String catalog2Id;
}
