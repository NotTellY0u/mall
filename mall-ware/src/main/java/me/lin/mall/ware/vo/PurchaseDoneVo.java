package me.lin.mall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author Fibonacci
 * @Date 2021/1/19 9:21 下午
 * @Version 1.0
 */
@Data
public class PurchaseDoneVo {
    // 采购单id
    @NotNull
    private Long id;
    //
    private List<PurchaseItemDoneVo> items;
}
