package me.lin.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.to.mq.StockLockedTo;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.ware.entity.WareSkuEntity;
import me.lin.mall.ware.vo.SkuHasStockVo;
import me.lin.mall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 22:59:58
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 判断是否有库存
     * @param skuIds skuID
     * @return 返回信息
     */
    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);

    /**
     * 为某个订单锁定库存
     * @param vo 订单信息
     * @return 结果
     */
    boolean orderLockStock(WareSkuLockVo vo);

    void unlock(StockLockedTo stockLockedTo);

    void unlock(OrderTo orderTo);
}

