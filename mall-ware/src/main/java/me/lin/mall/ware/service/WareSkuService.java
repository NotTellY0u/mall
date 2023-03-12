package me.lin.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.to.mq.OrderTo;
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
 * @author Ethan
 * @email hongshengmo@163.com
 * @date 2020-05-27 23:15:25
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStocks(List<Long> ids);

    Boolean orderLockStock(WareSkuLockVo lockVo);

    void unlock(StockLockedTo stockLockedTo);

    void unlock(OrderTo orderTo);
}

