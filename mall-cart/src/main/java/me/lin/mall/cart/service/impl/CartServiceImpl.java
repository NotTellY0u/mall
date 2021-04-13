package me.lin.mall.cart.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.lin.mall.cart.service.CartService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author Fibonacci
 * @Date 2021/4/13 11:15 上午
 * @Version 1.0
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    final StringRedisTemplate redisTemplate;

    public CartServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


}
