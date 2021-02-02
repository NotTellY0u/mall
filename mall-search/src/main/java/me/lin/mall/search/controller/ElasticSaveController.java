package me.lin.mall.search.controller;

import lombok.extern.slf4j.Slf4j;
import me.lin.mall.common.exception.BizCodeEnum;
import me.lin.mall.common.to.es.SkuEsModel;
import me.lin.mall.common.utils.R;
import me.lin.mall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-01-27 15:55
 * @Version 1.0
 */
@Slf4j
@RequestMapping("/search")
@RestController
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    /**
     * 上架商品
     */
    @PostMapping("save/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean b;
        try {
            b = productSaveService.productStatusUp(skuEsModels);
        } catch (Exception e) {
            log.error("ElasticSaveController商品上架错误");
            e.printStackTrace();
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (!b) {
            return R.ok();
        } else {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }

}
