package me.lin.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import me.lin.mall.cart.feign.ProductFeignService;
import me.lin.mall.cart.interceptor.CartInterceptor;
import me.lin.mall.cart.service.CartService;
import me.lin.mall.cart.vo.Cart;
import me.lin.mall.cart.vo.CartItem;
import me.lin.mall.cart.vo.SkuInfoVo;
import me.lin.mall.cart.vo.UserInfoTo;
import me.lin.mall.common.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author Fibonacci
 * @Date 2021/4/13 11:15 上午
 * @Version 1.0
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    final StringRedisTemplate redisTemplate;

    private static final String CART_PZREFIX = "linmall:cart:";
    final ProductFeignService productFeignService;
    final ThreadPoolExecutor poolExecutor;

    public CartServiceImpl(StringRedisTemplate redisTemplate,
                           ProductFeignService productFeignService,
                           ThreadPoolExecutor threadPoolExecutor) {
        this.redisTemplate = redisTemplate;
        this.productFeignService = productFeignService;
        this.poolExecutor = threadPoolExecutor;
    }

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            //购物车无此商品
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // 1.远程查询当前要添加的商品信息
                R skuInfo = productFeignService.getSkuInfo(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                // 2.商品添加到购物车

                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setPrice(data.getPrice());
                cartItem.setSkuId(skuId);
            }, poolExecutor);
            // 3.远程查询sku的组合信息
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> saleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(saleAttrValues);
            }, poolExecutor);

            CompletableFuture.allOf(future, getSkuSaleAttrValues).get();

            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        } else {
            // 购物车有此商品，修改数量
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));

            return cartItem;
        }


    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(o, CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            //登录
            String cartKey = CART_PZREFIX + userInfoTo.getUserId();
            // 2.如果临时购物车数据没有合并
            List<CartItem> tempCartItems = getCartItems(CART_PZREFIX + userInfoTo.getUserKey());
            if (tempCartItems != null) {
                //临时数据库有数据，需要合并
                for (CartItem tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
            }
            //3.获取登录后的购物车的数据
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        } else {
            // 未登录
            String cartKey = CART_PZREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);

        }
        return cart;
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    /**
     * 获取要操作的购物车
     *
     * @return 操作对象
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        String cartKey = "";

        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PZREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PZREFIX + userInfoTo.getUserKey();
        }

        return redisTemplate.boundHashOps(cartKey);
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(cartKey);
        List<Object> values = ops.values();
        if (values != null && values.size() > 0) {
            List<CartItem> cartItems = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItems;
        }
        return null;
    }
}
