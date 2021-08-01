package me.lin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.product.entity.SpuInfoDescEntity;
import me.lin.mall.product.entity.SpuInfoEntity;
import me.lin.mall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存spu信息
     * @param vo 要保存的信息
     */
    void saveSpuInfo(SpuSaveVo vo);

    /**
     * 保存spu信息
     * @param spuInfoEntity spu信息
     */
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);


    /**
     * 条件查询
     * @param params 查询条件
     * @return
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 上架商品
     * @param spuId spuId
     */
    void up(Long spuId);

    SpuInfoEntity getSpuInfoBySkuId();
}

