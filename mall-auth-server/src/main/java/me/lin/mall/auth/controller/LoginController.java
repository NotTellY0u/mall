package me.lin.mall.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author Fibonacci
 * @Date 2021/3/25 10:44 上午
 * @Version 1.0
 */
@Controller
public class LoginController {
    @GetMapping("/login.html")
    public String loginPage(){
        System.out.println("login");
        return "index";
    }

    @GetMapping("/reg.html")
    public String regPage(){
        System.out.println("reg");
        return "reg";
    }
}
