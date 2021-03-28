package me.lin.mall.thirdparty.controller;

import me.lin.mall.common.utils.R;
import me.lin.mall.thirdparty.component.SmsComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Fibonacci
 * @Date 2021/3/26 1:48 下午
 * @Version 1.0
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {

    final
    SmsComponent smsComponent;

    public SmsSendController(SmsComponent smsComponent) {
        this.smsComponent = smsComponent;
    }

    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        smsComponent.sendSmsCode(phone, code);
        System.out.println("CODE" + code);
        return R.ok();
    }
}
