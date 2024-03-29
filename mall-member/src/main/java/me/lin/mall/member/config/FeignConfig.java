package me.lin.mall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 解决Feign远程调用丢失请求头的问题
 *
 * @Author Fibonacci
 * @Date 2021/5/24 8:30 下午
 * @Version 1.0
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {

            @Override
            public void apply(RequestTemplate requestTemplate) {
                // RequestContextHolder拿到请求信息
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    // 同步请求头数据，Cookie
                    if (request != null) {
                        String cookie = request.getHeader("Cookie");
                        //给新请求同步老请求cookie
                        requestTemplate.header("Cookie", cookie);
                    }
                }
            }
        };
    }
}
