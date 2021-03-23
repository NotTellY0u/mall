package me.lin.mall.product.vo;

import lombok.Data;
import me.lin.mall.product.entity.SkuImagesEntity;
import me.lin.mall.product.entity.SkuInfoEntity;
import me.lin.mall.product.entity.SpuInfoDescEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author Fibonacci
 * @Date 2021/3/21 2:36 下午
 * @Version 1.0
 */
@Data
public class SkuItemVo {
    SkuInfoEntity skuInfoEntity;
    List<SkuImagesEntity> images;
    List<SkuItemSaleAttrVo> saleAttr;
    SpuInfoDescEntity desp;
    List<SpuItemAttrGroupVo> groupAttrs;
    boolean hasStock = true;

}
