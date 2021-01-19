package me.lin.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.ware.entity.PurchaseEntity;
import me.lin.mall.ware.vo.MergeVo;

import java.util.Map;

/**
 * 采购信息
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 22:59:59
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取未领取的采购单
     * @param params 采购单信息
     * @return 未领取的采购单
     */
    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    /**
     * 合并采购单
     * @param mergeVo 需要合并的采购单
     */
    void mergePurchase(MergeVo mergeVo);
}

