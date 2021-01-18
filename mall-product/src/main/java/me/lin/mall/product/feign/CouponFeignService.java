package me.lin.mall.product.feign;

import me.lin.mall.common.to.SkuReductionTo;
import me.lin.mall.common.to.SpuBoundsTo;
import me.lin.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author Fibonacci
 * @create: 2021-01-18 16:54
 * @Version 1.0
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {

    /**
     * 保存积分信息
     * @param spuBoundsTo to实体类
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
