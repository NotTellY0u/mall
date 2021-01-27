package me.lin.mall.search.controller;

import me.lin.mall.common.to.es.SkuEsModel;
import me.lin.mall.common.utils.R;
import me.lin.mall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-01-27 15:55
 * @Version 1.0
 */
@RequestMapping("/search")
@RestController
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;
    /**
     * 上架商品
     */
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){
        productSaveService.productStatusUp(skuEsModels);
        return R.ok();
    }

}
