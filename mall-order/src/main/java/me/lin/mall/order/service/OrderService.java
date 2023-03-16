package me.lin.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.order.entity.OrderEntity;
import me.lin.mall.order.vo.OrderConfirmVo;
import me.lin.mall.order.vo.OrderSubmitVo;
import me.lin.mall.order.vo.PayVo;
import me.lin.mall.order.vo.SubmitOrderResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 22:55:51
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页返回需要用的数据
     *
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     *  下单
     * @param vo 订单信息
     * @return 结果
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    /**
     * 根据订单号获取订单
     * @param orderSn 订单号
     * @return 订单信息
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 关闭订单
     * @param entity 订单信息
     */
    void closeOrder(OrderEntity entity);

    PayVo getOrderPay(String orderSn);
}

