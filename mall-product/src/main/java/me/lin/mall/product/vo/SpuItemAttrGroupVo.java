package me.lin.mall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author Fibonacci
 * @Date 2021/3/23 9:39 上午
 * @Version 1.0
 */
@Data
@ToString
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}
