package me.lin.mall.product.vo;

import lombok.Data;

/**
 * @Author Fibonacci
 * @create: 2021-01-14 14:48
 * @Version 1.0
 */
@Data
public class AttrRespVo extends AttrVo{
    /**
     * 分类名字
     */
    private String catelogName;
    /**
     * 分组名字
     */
    private String groupName;
    /**
     * 详细路径
     */
    private Long[] catelogPath;
}
