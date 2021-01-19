package me.lin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存sku信息
     * @param skuInfoEntity sku实体类
     */
    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    /**
     * 条件分页查询
     * @param params  分页条件
     * @return 查询结果
     */
    PageUtils queryPageByCondition(Map<String, Object> params);
}

