package me.lin.mall.product.feign;

import me.lin.mall.common.to.es.SkuEsModel;
import me.lin.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-02-01 10:09
 * @Version 1.0
 */
@FeignClient("mall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);

}
