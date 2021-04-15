package me.lin.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;
import me.lin.mall.product.dao.SkuSaleAttrValueDao;
import me.lin.mall.product.entity.SkuSaleAttrValueEntity;
import me.lin.mall.product.service.SkuSaleAttrValueService;
import me.lin.mall.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        List<SkuItemSaleAttrVo> saleAttrVos = this.baseMapper.getSaleAttrsBySpuId(spuId);
        return saleAttrVos;


    }

    @Override
    public List<String> getSkuSaleAttrValuesAsStringList(Long skuId) {
        return this.baseMapper.getSkuSaleAttrValuesAsStringList(skuId);
    }

}