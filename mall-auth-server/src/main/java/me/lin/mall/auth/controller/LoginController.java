package me.lin.mall.auth.controller;

import me.lin.mall.auth.feign.ThirdPartyFeignService;
import me.lin.mall.auth.vo.UserRegistVo;
import me.lin.mall.common.constant.AuthServerConstant;
import me.lin.mall.common.exception.BizCodeEnum;
import me.lin.mall.common.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author Fibonacci
 * @Date 2021/3/25 10:44 上午
 * @Version 1.0
 */
@Controller
public class LoginController {

    final ThirdPartyFeignService thirdPartyFeignService;

    final StringRedisTemplate redisTemplate;

    public LoginController(ThirdPartyFeignService thirdPartyFeignService, StringRedisTemplate redisTemplate) {
        this.thirdPartyFeignService = thirdPartyFeignService;
        this.redisTemplate = redisTemplate;
    }

    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

        // 1.接口防刷

        // 2.验证码校验

        String isCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CHACHE_PREFIX + phone);
        if (StringUtils.isNotBlank(isCode)) {
            long l = Long.parseLong(isCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }

        }

        int s = (int) ((Math.random() * 9 + 1) * 100000);
        String code = s + "_" + System.currentTimeMillis();
        System.out.println("UUID" + code);

        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CHACHE_PREFIX + phone, code, 10, TimeUnit.MINUTES);

        thirdPartyFeignService.sendCode(phone, code);
        return R.ok();
    }

    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo registVo, BindingResult result, Model model) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField, FieldError::getDefaultMessage));

            model.addAttribute("errors", errors);
            return "forward:/reg.html";
        }
        return "redirect:/login.html";
    }
}
