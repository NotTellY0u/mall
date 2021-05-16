package me.lin.mall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author Fibonacci
 * @Date 2021/5/16 4:39 下午
 * @Version 1.0
 */
@Controller
public class OrderWebController {

    @GetMapping("/toTrade")
    public String toTrade() {
        return "confirm";
    }
}
