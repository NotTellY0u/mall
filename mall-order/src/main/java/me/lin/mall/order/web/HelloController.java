package me.lin.mall.order.web;

import me.lin.mall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * @Author Fibonacci
 * @Date 2021/4/26 8:26 下午
 * @Version 1.0
 */

@Controller
public class HelloController {

    @Resource
    RabbitTemplate rabbitTemplate;


    @ResponseBody
    @GetMapping("/test/createOrder")
    public String createOrderTest(){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        orderEntity.setModifyTime(new Date());
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderEntity);
        return "OK";
    }


    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page) {
        return page;
    }
}
