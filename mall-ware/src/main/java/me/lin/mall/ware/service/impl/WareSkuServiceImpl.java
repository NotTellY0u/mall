package me.lin.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.lin.mall.common.exception.NoStockException;
import me.lin.mall.common.to.mq.StockDetailTo;
import me.lin.mall.common.to.mq.StockLockedTo;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;
import me.lin.mall.common.utils.R;
import me.lin.mall.ware.dao.WareSkuDao;
import me.lin.mall.ware.entity.WareOrderTaskDetailEntity;
import me.lin.mall.ware.entity.WareOrderTaskEntity;
import me.lin.mall.ware.entity.WareSkuEntity;
import me.lin.mall.ware.feign.OrderFeignService;
import me.lin.mall.ware.feign.ProductFeignService;
import me.lin.mall.ware.service.WareOrderTaskDetailService;
import me.lin.mall.ware.service.WareOrderTaskService;
import me.lin.mall.ware.service.WareSkuService;
import me.lin.mall.ware.vo.OrderItemVo;
import me.lin.mall.ware.vo.SkuHasStockVo;
import me.lin.mall.ware.vo.WareSkuLockVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RabbitListener(queues = "stock.release.stock.queue")
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    WareSkuDao wareSkuDao;

    @Resource
    ProductFeignService productFeignService;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    WareOrderTaskService orderTaskService;

    @Resource
    WareOrderTaskDetailService orderTaskDetailService;


    @Resource
    OrderFeignService orderFeignService;




    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> skuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (skuEntities == null || skuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //远程查询sku名字,如果失败无需回滚
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    /**
     * 判断是否有库存
     *
     * @param skuIds skuID
     * @return 返回信息
     */
    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count != null && count > 0);
            log.info("HasStock的值" + vo.getHasStock());
            log.info("SkuId的值" + vo.getSkuId());
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 为某个订单锁定库存
     * rollbackFor=NoStockException.class
     * 默认只要是运行时异常都回滚
     *
     * @param vo 订单信息
     *           <p>
     *           解锁库存场景
     *           1)、下订单成功，订单过期没有支付被系统自动取消、被用户手动取消。都要解锁
     *           2)、下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚，之前锁定的库存就要自动就锁
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean orderLockStock(WareSkuLockVo vo) {

        /**
         * 保存工作单的详情
         * 追溯
         */
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(taskEntity);

        List<OrderItemVo> locks = vo.getLocks();

        // 1.找到每个商品在哪个仓库有库存
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            skuWareHasStock.setNum(item.getCount());
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            skuWareHasStock.setWareId(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());

        // 2.锁定库存
        for (SkuWareHasStock skuWareHasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareIds = skuWareHasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                throw new NoStockException(skuId);
            }
            // 如果每一个商品都锁定成功，将当前商品锁定了几件的工作单记录发给MQ
            // 锁定失败，前面保存的工作单信息就回滚了。发送出去的消息，即使要解锁记录，由于去数据库查不到id，所以就不用解锁
            for (Long wareId : wareIds) {
                // 成功就返回1，否则就是0
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, skuWareHasStock.getNum());
                if (count == 1) {
                    skuStocked = true;
                    //TODO 告诉MQ库存锁定成功
                    WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, "", skuWareHasStock.getNum(), taskEntity.getId(), wareId, 1);
                    orderTaskDetailService.save(taskDetailEntity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(taskEntity.getId());
                    StockDetailTo detail = new StockDetailTo();
                    BeanUtils.copyProperties(taskDetailEntity, detail);
                    // 只发id不行，防止回滚以后找不到数据
                    stockLockedTo.setDetail(detail);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.lockd", stockLockedTo);
                    break;
                } else {
                    //当前仓库锁失败，重试下一个仓库
                }
                if (skuStocked == false) {
                    throw new NoStockException(skuId);
                }
            }
        }

        return true;
    }


    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}