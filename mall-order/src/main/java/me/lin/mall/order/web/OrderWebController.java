package me.lin.mall.order.web;

import me.lin.mall.order.service.OrderService;
import me.lin.mall.order.vo.OrderConfirmVo;
import me.lin.mall.order.vo.OrderSubmitVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.ExecutionException;

/**
 * @Author Fibonacci
 * @Date 2021/5/16 4:39 下午
 * @Version 1.0
 */
@Controller
public class OrderWebController {

    final
    OrderService orderService;

    public OrderWebController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmVo = orderService.confirmOrder();

        model.addAttribute("orderConfirmData", confirmVo);

        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo){
        //TODO
        //下单：去创建订单，验令牌，验价格，锁库存。。。
        //下单成功来到支付选择页
        //下单失败回到订单确认页重新确认订单信息
        return null;
    }

}
