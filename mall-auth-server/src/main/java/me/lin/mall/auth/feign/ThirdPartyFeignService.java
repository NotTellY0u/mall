package me.lin.mall.auth.feign;

import me.lin.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author Fibonacci
 * @Date 2021/3/26 2:03 下午
 * @Version 1.0
 */
@FeignClient("mall-third-party")
public interface ThirdPartyFeignService {
    @GetMapping("/sms/sendCode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);

}
