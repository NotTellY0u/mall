package me.lin.mall.cart.service;

import me.lin.mall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @Author Fibonacci
 * @Date 2021/4/13 11:14 上午
 * @Version 1.0
 */
public interface CartService {

    /**
     * 添加到购物车
     *
     * @param skuId 商品id
     * @param num   数量
     * @return 添加到购物车的商品信息
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车商品
     *
     * @param skuId 商品id
     * @return 购物车商品
     */
    CartItem getCartItem(Long skuId);
}
