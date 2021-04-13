package me.lin.mall.cart.controller;

import me.lin.mall.cart.interceptor.CartInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author Fibonacci
 * @Date 2021/4/13 1:52 下午
 * @Version 1.0
 */
@Controller
public class CartController {

    @GetMapping("/cart.html")
    public String cartListPage() {

        CartInterceptor.threadLocal.get();

        return "cartList";
    }
}
