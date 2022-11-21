package me.lin.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.lin.mall.common.exception.NoStockException;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;
import me.lin.mall.common.utils.R;
import me.lin.mall.common.vo.MemberRespVo;
import me.lin.mall.order.constant.OrderConstant;
import me.lin.mall.order.dao.OrderDao;
import me.lin.mall.order.entity.OrderEntity;
import me.lin.mall.order.entity.OrderItemEntity;
import me.lin.mall.order.enume.OrderStatusEnum;
import me.lin.mall.order.feign.CartFeignService;
import me.lin.mall.order.feign.MemberFeignService;
import me.lin.mall.order.feign.ProductFeignService;
import me.lin.mall.order.feign.WmsFeignService;
import me.lin.mall.order.interceptor.LoginUserInterceptor;
import me.lin.mall.order.service.OrderItemService;
import me.lin.mall.order.service.OrderService;
import me.lin.mall.order.to.OrderCreateTo;
import me.lin.mall.order.vo.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    final
    OrderItemService orderItemService;

    final
    ThreadPoolExecutor threadPoolExecutor;

    final
    MemberFeignService memberFeignService;

    final
    CartFeignService cartFeignService;

    final
    WmsFeignService wmsFeignService;

    final
    ProductFeignService productFeignService;

    final
    StringRedisTemplate redisTemplate;

    private final ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();
    public OrderServiceImpl(MemberFeignService memberFeignService, CartFeignService cartFeignService, ThreadPoolExecutor threadPoolExecutor, WmsFeignService wmsFeignService, StringRedisTemplate redisTemplate, ProductFeignService productFeignService, OrderItemService orderItemService) {
        this.memberFeignService = memberFeignService;
        this.cartFeignService = cartFeignService;
        this.threadPoolExecutor = threadPoolExecutor;
        this.wmsFeignService = wmsFeignService;
        this.redisTemplate = redisTemplate;
        this.productFeignService = productFeignService;

        this.orderItemService = orderItemService;
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

    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        confirmVoThreadLocal.set(vo);
        // 1.验证令牌【令牌的对比和删除必须保证原子性】
        String script= "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        //原子验证令牌和删除令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if(result == null) {
            throw new NullPointerException();
        }else {
            if (result == 0L) {
                // 令牌验证失败
                responseVo.setCode(1);
                return responseVo;
            } else {
                //令牌验证成功
                //下单：去创建订单，验令牌，验价格，锁库存。。。
                // 1.创建订单、订单项等信息
                OrderCreateTo order = createOrder();
                // 2.验价
                BigDecimal payAmount = order.getOrder().getPayAmount();
                BigDecimal payPrice = vo.getPayPrice();
                if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                    // 金额对比

                    // 3.保存订单
                    saveOrder(order);
                    // 4.库存锁定
                    WareSkuLockVo lockVo = new WareSkuLockVo();
                    lockVo.setOrderSn(order.getOrder().getOrderSn());
                    List<OrderItemVo> collect = order.getOrderItems().stream().map(item -> {
                        OrderItemVo orderItemVo = new OrderItemVo();
                        orderItemVo.setSkuId(item.getSkuId());
                        orderItemVo.setCount(item.getSkuQuantity());
                        orderItemVo.setTitle(item.getSkuName());
                        return orderItemVo;
                    }).collect(Collectors.toList());
                    lockVo.setLocks(collect);
                    R r = wmsFeignService.orderLockStock(lockVo);
                    if(r.getCode() == 0){
                        //库存锁定成功
                        responseVo.setOrderEntity(order.getOrder());
                        responseVo.setCode(0);
                        return responseVo;
                    }else {
                        //锁定失败
                        throw new NoStockException(order.getOrder().getId());
                    }
                }else {
                    responseVo.setCode(2);
                    return responseVo;
                }

            }
        }

    }

    /**
     * 保存订单数据
     * @param order 订单信息
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OrderItemEntity> orderItems = order.getOrderItems();

        orderItemService.saveBatch(orderItems);


    }

    private OrderCreateTo createOrder(){

        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //1.生成订单号
        String orderSn = IdWorker.getTimeId();
        // 创建订单号
        OrderEntity orderEntity = buildOrder(orderSn);

        // 2. 获取所有订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);

        // 3. 计算相关价格
        computePrice(orderEntity, Objects.requireNonNull(itemEntities));
        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItems(itemEntities);

        return orderCreateTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {

        BigDecimal total = new BigDecimal("0.0");

        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        //订单总额，叠加每一个订单项的总额
        BigDecimal giftIntegration = new BigDecimal("0.0");
        BigDecimal giftGrowth = new BigDecimal("0.0");
        for(OrderItemEntity entity : itemEntities){
            BigDecimal realAmount = entity.getRealAmount();
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(realAmount);
            giftIntegration = giftIntegration.add(new BigDecimal(entity.getGiftIntegration()));
            giftGrowth = giftGrowth.add(new BigDecimal(entity.getGiftGrowth()));

        }
        // 1.订单价格相关
        orderEntity.setTotalAmount(total);
        // 设置应付总额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setPromotionAmount(promotion);

        //设置积分登记信息
        orderEntity.setIntegration(giftIntegration.intValue());
        orderEntity.setGrowth(giftGrowth.intValue());
        // 未删除
        orderEntity.setDeleteStatus(0);


    }

    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(memberRespVo.getId());
        // 获取收货地址信息
        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();

        confirmVoThreadLocal.remove();
        // 获取地址信息
        R fare = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResp = fare.getData(new TypeReference<FareVo>() {
        });

        BigDecimal respFare = fareResp.getFare();
        // 设置运费信息
        entity.setFreightAmount(respFare);
        // 设置收货人信息
        entity.setReceiverCity(fareResp.getAddress().getCity());
        entity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        entity.setReceiverName(fareResp.getAddress().getName());
        entity.setReceiverPhone(fareResp.getAddress().getPhone());
        entity.setReceiverPostCode(fareResp.getAddress().getPostCode());
        entity.setReceiverProvince(fareResp.getAddress().getProvince());
        entity.setReceiverRegion(fareResp.getAddress().getRegion());

        // 设置订单状态
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setConfirmStatus(0);
        entity.setAutoConfirmDay(7);

        return entity;
    }

    /**
     * 构建所有订单项
     * @return 订单项
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //最后确定每个购物项的价格
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();

        if(currentUserCartItems != null && currentUserCartItems.size() > 0){
            return currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                ;
                Objects.requireNonNull(itemEntity).setOrderSn(orderSn);
//                itemEntity.setIntegrationAmount();
                return itemEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 构建某一订单项
     * @param cartItem 购物车
     * @return 订单项
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem){
        OrderItemEntity itemEntity = new OrderItemEntity();

        //1.订单信息：订单号

        //2.商品的spu信息
        Long skuId = cartItem.getSkuId();
        System.out.println("skuId"+skuId);
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());
        //3.商品的sku信息
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(cartItem.getCount());
        //4.优惠信息

        //5.积分信息
        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().intValue());

        // 6.订单项的价格信息
        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));
        //当前订单项的实际金额
        BigDecimal origin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal decimal = origin.subtract(itemEntity.getCouponAmount()).subtract(itemEntity.getPromotionAmount()).subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(decimal);

        return itemEntity;
    }
}