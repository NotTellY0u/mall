package me.lin.mall.cart.service;

import me.lin.mall.cart.vo.Cart;
import me.lin.mall.cart.vo.CartItem;

import java.util.List;
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

    /**
     * 获取整个购物车
     *
     * @return 购物车信息
     */
    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 勾选购物车
     *
     * @param skuId 商品id
     * @param check 是否勾选
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 修改购物项数量
     *
     * @param skuId 商品id
     * @param num   商品数量
     */
    void changeItemCount(Long skuId, Integer num);

    /**
     * 删除指定购物项
     *
     * @param skuId 商品id
     */
    void deleteItem(Long skuId);

    /**
     * 获取用户购物车信息
     *
     * @return
     */
    List<CartItem> getUserCartItems();
}
