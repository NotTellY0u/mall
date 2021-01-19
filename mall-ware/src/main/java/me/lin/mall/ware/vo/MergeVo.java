package me.lin.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-01-19 16:01
 * @Version 1.0
 */
@Data
public class MergeVo {
    /**
     * 整单id
     */
    private Long purchaseId;
    /**
     * 合并项集合
     */
    private List<Long> items;
}
