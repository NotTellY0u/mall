package me.lin.mall.order.config;

import com.rabbitmq.client.Channel;
import me.lin.mall.order.entity.OrderEntity;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;

@Configuration
public class MyMQConfig {

    @RabbitListener(queues = "order.release.order.queue")
    public void listener(OrderEntity entity, Channel channel, Message message){
        System.out.println("收到过期消息准备关闭订单"+entity.getOrderSn());
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 容器中的bingding，Queue，Exchange都会自动创建（RabbitMQ没有的情况下）
     * RabbitMQ只要有。@Bean声明属性发生变化也不会覆盖
     * @return
     */

    @Bean
    public Queue orderDelayQueue(){
        HashMap<String, Object> arguments = new HashMap<>(128);
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 60000);
        return new Queue("order.delay.queue", true, false, false,arguments);
    }

    @Bean
    public Queue orderReleaseOrderQueue(){
        return new Queue("order.release.order.queue", true, false, false);
    }

    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange", true, false);
    }

    @Bean
    public Binding orderCreateOrderBinding(){
        return new Binding("order.delay.queue",Binding.DestinationType.QUEUE,"order-event-exchange","order.create.order",null);
    }

    @Bean
    public Binding orderReleaseOrderBinding(){
        return new Binding("order.release.order.queue",Binding.DestinationType.QUEUE,"order-event-exchange","order.release.order",null);
    }

}
