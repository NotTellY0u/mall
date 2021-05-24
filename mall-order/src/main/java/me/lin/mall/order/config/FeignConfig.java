package me.lin.mall.order.config;

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
                HttpServletRequest request = requestAttributes != null ? requestAttributes.getRequest() : null;
                // 同步请求头数据，Cookie
                String cookie = request != null ? request.getHeader("Cookie") : null;

                requestTemplate.header("Cookie", cookie);

            }
        };
    }
}
