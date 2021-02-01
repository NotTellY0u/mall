package me.lin.mall.search.service;

import me.lin.mall.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-01-27 16:17
 * @Version 1.0
 */
public interface ProductSaveService {

    /**
     * 保存上架状态
     * @param skuEsModels 需要保存状态的商品
     * @return
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
