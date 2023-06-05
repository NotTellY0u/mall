package me.lin.mall.seckill.feign;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("mall-coupon")
public interface CouponFeignService {
}
