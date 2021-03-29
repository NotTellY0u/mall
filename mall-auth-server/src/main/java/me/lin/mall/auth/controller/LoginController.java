package me.lin.mall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import me.lin.mall.auth.feign.MemberFeignService;
import me.lin.mall.auth.feign.ThirdPartyFeignService;
import me.lin.mall.auth.vo.UserLoginVo;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
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

    final MemberFeignService memberFeignService;

    public LoginController(ThirdPartyFeignService thirdPartyFeignService, StringRedisTemplate redisTemplate, MemberFeignService memberFeignService) {
        this.thirdPartyFeignService = thirdPartyFeignService;
        this.redisTemplate = redisTemplate;
        this.memberFeignService = memberFeignService;
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

        thirdPartyFeignService.sendCode(phone, String.valueOf(s));
        return R.ok();
    }

    /**
     * 重定向携带数据，利用session原理，将数据放在session中
     * RedirectAttributes redirectAttributes: 模拟重定向携带数据
     *
     * @param registVo 登录信息
     * @param result   错误结果
     * @param model    模型
     * @return 注册结果
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo registVo, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField, FieldError::getDefaultMessage));

//            model.addAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.linmall.com/reg.html";
        }

        // 1.真正注册，调用远程服务进行注册

        // 1.校验验证码
        String code = registVo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CHACHE_PREFIX + registVo.getPhone());
        if (StringUtils.isNotBlank(s)) {
            String split = s.split("_")[0];
            if (code.equals(split)) {
                // 删除验证码，令牌机制
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CHACHE_PREFIX + registVo.getPhone());
                R r = memberFeignService.regist(registVo);
                if (r.getCode() == 0) {
                    return "redirect:http://auth.linmall.com/login.html";
                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg", new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.linmall.com/reg.html";
                }
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
//                  model.addAttribute("errors", errors);
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.linmall.com/reg.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
//                model.addAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.linmall.com/reg.html";
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo loginVo, RedirectAttributes attributes) {

        R r = memberFeignService.login(loginVo);
        if (r.getCode() == 0) {
            return "redirect:http://linmall.com/";
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {
            }));
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.linmall.com/login.html";
        }


    }
}
