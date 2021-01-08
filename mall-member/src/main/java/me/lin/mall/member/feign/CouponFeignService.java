package me.lin.mall.member.feign;

import me.lin.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Fibonacci
 * @Date 2020/12/13 1:24 下午
 * @Version 1.0
 * 这是一个声明式的远程调用服务
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {

    @RequestMapping("coupon/coupon/member/list")
    public R memberCoupons();
}
