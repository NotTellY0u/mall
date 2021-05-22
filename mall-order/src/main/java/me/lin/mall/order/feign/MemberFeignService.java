package me.lin.mall.order.feign;

import me.lin.mall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author Fibonacci
 * @Date 2021/5/17 7:49 下午
 * @Version 1.0
 */
@FeignClient("mall-member")
public interface MemberFeignService {
    @GetMapping("member/memberreceiveaddress/{memberId}/addresses")
    List<MemberAddressVo> getAddress(@PathVariable Long memberId);
}
