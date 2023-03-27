package me.lin.mall.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberWebController {

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(){

        // 查出当前登录用户所有订单列表数据
        return "orderList";
    }

}
