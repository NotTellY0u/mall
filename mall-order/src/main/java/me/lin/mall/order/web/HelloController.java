package me.lin.mall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Fibonacci
 * @Date 2021/4/26 8:26 下午
 * @Version 1.0
 */

@Controller
public class HelloController {

    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page) {
        return page;
    }
}
