package me.lin.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;
import me.lin.mall.ware.dao.PurchaseDetailDao;
import me.lin.mall.ware.entity.PurchaseDetailEntity;
import me.lin.mall.ware.service.PurchaseDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(StringUtils.isNotBlank(key)){
            queryWrapper.and(wrapper->{
                wrapper.eq("purchase_id",key).or().eq("sku_id",key);
            });
        }
        String status = (String) params.get("status");
        if(StringUtils.isNotBlank(status)){
            queryWrapper.eq("status",status);
        }
        String wareId = (String) params.get("wareId");
        if(StringUtils.isNotBlank(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 通过id获取采购项信息
     * @param id 采购单id
     * @return 采购项信息列表
     */
    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        List<PurchaseDetailEntity> detailEntities = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
        return detailEntities;
    }

}