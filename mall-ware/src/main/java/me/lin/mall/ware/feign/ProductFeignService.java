package me.lin.mall.ware.feign;

import me.lin.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Fibonacci
 * @create: 2021-01-20 09:52
 * @Version 1.0
 */
@FeignClient("mall-product")
public interface ProductFeignService {

    /**
     * @param skuId skuId
     * @return R
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
