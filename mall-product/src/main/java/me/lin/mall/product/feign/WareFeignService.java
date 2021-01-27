package me.lin.mall.product.feign;

import me.lin.mall.common.utils.R;
import me.lin.mall.product.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-01-27 14:10
 * @Version 1.0
 */
@FeignClient("mall-ware")
public interface WareFeignService {

    @PostMapping("ware/waresku/hasstock")
    R<List<SkuHasStockVo>> getSkusHasStock(@RequestBody List<Long> skuIds);
}
