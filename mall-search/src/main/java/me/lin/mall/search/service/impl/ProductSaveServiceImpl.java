package me.lin.mall.search.service.impl;

import me.lin.mall.common.to.es.SkuEsModel;
import me.lin.mall.search.service.ProductSaveService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-01-27 16:26
 * @Version 1.0
 */
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    /**
     * 保存上架状态
     *
     * @param skuEsModels 需要保存状态的商品
     */
    @Override
    public void productStatusUp(List<SkuEsModel> skuEsModels) {

    }
}
