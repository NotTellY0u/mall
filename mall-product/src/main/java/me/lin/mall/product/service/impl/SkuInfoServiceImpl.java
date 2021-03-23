package me.lin.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;
import me.lin.mall.product.dao.SkuInfoDao;
import me.lin.mall.product.entity.SkuImagesEntity;
import me.lin.mall.product.entity.SkuInfoEntity;
import me.lin.mall.product.entity.SpuInfoDescEntity;
import me.lin.mall.product.service.*;
import me.lin.mall.product.vo.Attr;
import me.lin.mall.product.vo.SkuItemSaleAttrVo;
import me.lin.mall.product.vo.SkuItemVo;
import me.lin.mall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * @author Fibonacci
 */
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    final
    SkuImagesService skuImagesService;

    final SpuInfoDescService spuInfoDescService;

    final AttrGroupService attrGroupService;

    final SkuSaleAttrValueService skuSaleAttrValueService;

    public SkuInfoServiceImpl(SkuImagesService skuImagesService, SpuInfoDescService spuInfoDescService, AttrGroupService attrGroupService,SkuSaleAttrValueService skuSaleAttrValueService) {
        this.skuImagesService = skuImagesService;
        this.spuInfoDescService = spuInfoDescService;
        this.attrGroupService = attrGroupService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存sku信息
     *
     * @param skuInfoEntity sku实体类
     */
    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    /**
     * 条件分页查询
     *
     * @param params 分页条件
     * @return 查询结果
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);

        }
        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (StringUtils.isNotBlank(min)) {
            queryWrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if (StringUtils.isNotBlank(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                    queryWrapper.le("price", max);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 通过spuId查询sku信息
     *
     * @param spuId spuId
     * @return sku信息
     */
    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

    /**
     * @param skuId 商品id
     * @return 页面信息
     */
    @Override
    public SkuItemVo item(Long skuId) {
        // 1.sku基本信息获取
        SkuItemVo skuItemVo = new SkuItemVo();
        SkuInfoEntity info = getById(skuId);
        skuItemVo.setSkuInfoEntity(info);
        Long catalogId = info.getCatalogId();
        Long spuId = info.getSpuId();
        // 2.sku图片信息
        List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(images);
        // 3.获取sku的销售属性组合
        List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
        skuItemVo.setSaleAttr(saleAttrVos);
        // 4.获取spu的介绍

        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
        skuItemVo.setDesp(spuInfoDescEntity);
        // 5.获取spu的规格参数信息
        List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId,catalogId);
        skuItemVo.setGroupAttrs(attrGroupVos);

        return skuItemVo;
    }

}