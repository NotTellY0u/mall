package me.lin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存商品属性
     * @param collect 属性集合
     */
    void saveProductAttr(List<ProductAttrValueEntity> collect);

    /**
     * 查询商品规格属性
     * @param spuId spuId
     * @return 规格属性列表
     */
    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

    /**
     * 更新spu属性
     * @param spuId spuId
     * @param entities 更新的属性
     */
    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
}

