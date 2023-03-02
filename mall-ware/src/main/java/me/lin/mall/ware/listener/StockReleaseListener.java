package me.lin.mall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import me.lin.mall.common.to.mq.StockDetailTo;
import me.lin.mall.common.to.mq.StockLockedTo;
import me.lin.mall.common.utils.R;
import me.lin.mall.ware.dao.WareSkuDao;
import me.lin.mall.ware.entity.WareOrderTaskDetailEntity;
import me.lin.mall.ware.entity.WareOrderTaskEntity;
import me.lin.mall.ware.feign.OrderFeignService;
import me.lin.mall.ware.service.WareOrderTaskDetailService;
import me.lin.mall.ware.service.WareOrderTaskService;
import me.lin.mall.ware.service.WareSkuService;
import me.lin.mall.ware.vo.OrderVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
@Slf4j
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {


    @Resource
    WareSkuService wareSkuService;

    @Resource
    OrderFeignService orderFeignService;

    @Resource
    WareOrderTaskDetailService orderTaskDetailService;

    @Resource
    WareOrderTaskService orderTaskService;

    @Resource
    WareSkuDao wareSkuDao;
    /**
     * 直接解锁库存的消息失败了一定要告诉服务解锁失败
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        try {

            log.info("收到解锁库存的信息。。。。。。。");
            StockDetailTo detail = to.getDetail();
            Long detailId = detail.getId();
            //解锁
            // 1.查询数据库关于这个订单的锁定库存信息
            // 有：证明库存锁定成功
            //     解锁看订单情况 1.没有订单，必须解锁  2.有订单。不是解锁库存  订单状态：已取消，解锁库存 没取消，不能解锁
            // 无：库存锁定失败，库存回滚，无需解锁
            WareOrderTaskDetailEntity orderTaskDetail = orderTaskDetailService.getById(detailId);
            if (orderTaskDetail != null) {
                // 解锁
                Long id = to.getId();
                WareOrderTaskEntity orderTask = orderTaskService.getById(id);
                // 根据订单号查询订单状态
                String orderSn = orderTask.getOrderSn();
                R r = orderFeignService.getOrderStatus(orderSn);
                if (r.getCode() == 0) {
                    // 订单数据返回成功
                    OrderVo data = r.getData(new TypeReference<OrderVo>() {
                    });

                    if (data == null || data.getStatus() == 4) {
                        // 订单已经被取消了
                        unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            } else {
                // 无需解锁
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    private void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        wareSkuDao.unlockStock(skuId, wareId, num);
    }
}
