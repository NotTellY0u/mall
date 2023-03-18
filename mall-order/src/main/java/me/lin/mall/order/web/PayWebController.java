package me.lin.mall.order.web;

import com.alipay.api.AlipayApiException;
import me.lin.mall.order.config.AlipayTemplate;
import me.lin.mall.order.service.OrderService;
import me.lin.mall.order.vo.PayVo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

public class PayWebController {

    @Resource
    private AlipayTemplate alipayTemplate;

    @Resource
    private OrderService orderService;

    @GetMapping(value = "/payOrder",produces = MediaType.TEXT_HTML_VALUE)
    public String payOrder(@RequestParam("orderSn")String orderSn) throws AlipayApiException {

        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        return pay;

    }

}
