package me.lin.mall.order.listener;


import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import me.lin.mall.common.to.mq.OrderTo;
import me.lin.mall.order.entity.OrderEntity;
import me.lin.mall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
@RabbitListener(queues = "order.release.order.queue")
@Slf4j
public class OrderCloseListener {

    @Resource
    OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
        log.info("收到过期的订单信息：准备关闭订单" + entity.getOrderSn());
        try {
            orderService.closeOrder(entity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }


}
