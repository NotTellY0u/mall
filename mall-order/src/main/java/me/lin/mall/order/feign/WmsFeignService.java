package me.lin.mall.order.feign;

import me.lin.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author Fibonacci
 * @Date 2021/5/30 3:50 下午
 * @Version 1.0
 */
@FeignClient("mall-ware")
public interface WmsFeignService {
    @PostMapping("ware/waresku/hasstock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);
}
