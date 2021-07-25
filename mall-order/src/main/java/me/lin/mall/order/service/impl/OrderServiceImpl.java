package me.lin.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;
import me.lin.mall.common.utils.R;
import me.lin.mall.common.vo.MemberRespVo;
import me.lin.mall.order.constant.OrderConstant;
import me.lin.mall.order.dao.OrderDao;
import me.lin.mall.order.entity.OrderEntity;
import me.lin.mall.order.feign.CartFeignService;
import me.lin.mall.order.feign.MemberFeignService;
import me.lin.mall.order.feign.WmsFeignService;
import me.lin.mall.order.interceptor.LoginUserInterceptor;
import me.lin.mall.order.service.OrderService;
import me.lin.mall.order.vo.MemberAddressVo;
import me.lin.mall.order.vo.OrderConfirmVo;
import me.lin.mall.order.vo.OrderItemVo;
import me.lin.mall.order.vo.SkuHasStockVo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    final
    ThreadPoolExecutor threadPoolExecutor;

    final
    MemberFeignService memberFeignService;

    final
    CartFeignService cartFeignService;

    final
    WmsFeignService wmsFeignService;

    final
    StringRedisTemplate redisTemplate;

    public OrderServiceImpl(MemberFeignService memberFeignService, CartFeignService cartFeignService, ThreadPoolExecutor threadPoolExecutor, WmsFeignService wmsFeignService,StringRedisTemplate redisTemplate) {
        this.memberFeignService = memberFeignService;
        this.cartFeignService = cartFeignService;
        this.threadPoolExecutor = threadPoolExecutor;
        this.wmsFeignService = wmsFeignService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmVo = new OrderConfirmVo();

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            //1.远程查询所有的收货地址
            RequestContextHolder.setRequestAttributes(requestAttributes);
            System.out.println(memberRespVo.getId());
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            confirmVo.setAddress(address);
        }, threadPoolExecutor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //2.远程查询购物车中所有选中的购物项
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        }, threadPoolExecutor).thenRunAsync(() -> {
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> collect = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R skusHasStock = wmsFeignService.getSkusHasStock(collect);
            List<SkuHasStockVo> data = skusHasStock.getData(new TypeReference<List<SkuHasStockVo>>() {
            });
            if (data != null) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        }, threadPoolExecutor);


        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        //防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);



        CompletableFuture.allOf(getAddressFuture, cartFuture).get();
        //4.其他数据自动计算

        return confirmVo;

    }

}