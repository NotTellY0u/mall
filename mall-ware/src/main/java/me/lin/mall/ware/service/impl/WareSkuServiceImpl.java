package me.lin.mall.ware.service.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.lin.mall.common.utils.R;
import me.lin.mall.ware.exception.NoStockException;
import me.lin.mall.ware.feign.ProductFeignService;
import me.lin.mall.ware.vo.OrderItemVo;
import me.lin.mall.ware.vo.SkuHasStockVo;
import me.lin.mall.ware.vo.WareSkuLockVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;

import me.lin.mall.ware.dao.WareSkuDao;
import me.lin.mall.ware.entity.WareSkuEntity;
import me.lin.mall.ware.service.WareSkuService;

import javax.annotation.Resource;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    WareSkuDao wareSkuDao;

    @Resource
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(StringUtils.isNotBlank(skuId)){
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if(StringUtils.isNotBlank(wareId)){
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
        if(skuEntities == null || skuEntities.size() == 0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //远程查询sku名字,如果失败无需回滚
            try{
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");
                if(info.getCode() == 0){
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            wareSkuDao.insert(wareSkuEntity);
        }else {
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }
    }

    /**
     * 判断是否有库存
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
            log.info("HasStock的值"+vo.getHasStock());
            log.info("SkuId的值"+vo.getSkuId());
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public boolean orderLockStock(WareSkuLockVo vo) {

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
            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId,skuWareHasStock.getNum());
                if( count == 1){
                    skuStocked = true;
                    break;
                }else{
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
    class SkuWareHasStock{
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}