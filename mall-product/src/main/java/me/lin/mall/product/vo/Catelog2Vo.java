package me.lin.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-02-08 14:05
 * @Version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {
    private String catelogId;
    private List<Object> catelog3List;
    private String name;
    private String id;
}
