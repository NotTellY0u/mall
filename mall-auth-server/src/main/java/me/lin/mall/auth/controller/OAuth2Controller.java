package me.lin.mall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.lin.mall.auth.feign.MemberFeignService;
import me.lin.mall.auth.vo.MemberRespVo;
import me.lin.mall.auth.vo.SocialUser;
import me.lin.mall.common.utils.HttpUtils;
import me.lin.mall.common.utils.R;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 *
 * @Author Fibonacci
 * @Date 2021/3/30 2:45 下午
 * @Version 1.0
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "api.weibo")
@Controller
public class OAuth2Controller {

    final MemberFeignService memberFeignService;
    private String clientId;
    private String clientSecret;
    private String grantType;
    private String redirectURI;

    public OAuth2Controller(MemberFeignService memberFeignService) {
        this.memberFeignService = memberFeignService;
    }

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam(value = "code") String code) throws Exception {
        System.out.println("进入/oauth2.0/weibo/success");
        //1.根据code换取accessToken
        HashMap<String, String> map = new HashMap<>();
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("grand_type", grantType);
        map.put("redirect_uri", redirectURI);
        map.put("code", code);
        Map<String, String> headers = new HashMap<>();
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", headers, null, map);
        if (response.getStatusLine().getStatusCode() == 200) {
            //获取到了accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            // 知道当前是哪个社交用户
            // 1）当前用户如果是第一次进网站，自动注册进来(为当前社交用户生成一个会员信息账号，以后这个社交账号就对应指定的会员）
            R oauthlogin = memberFeignService.oauthlogin(socialUser);
            if (oauthlogin.getCode() == 0) {
                MemberRespVo data = oauthlogin.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登陆成功：用户：「」", data.toString());
                return "redirect:http://linmall.com/";
            } else {
                return "redirect:http://auth.linmall.com/login.html";
            }

        } else {
            return "redirect:http://auth.linmall.com/login.html";
        }
    }
}
