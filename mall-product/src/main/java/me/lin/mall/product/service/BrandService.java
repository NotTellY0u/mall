package me.lin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.product.entity.BrandEntity;
import me.lin.mall.product.vo.BrandVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
public interface BrandService extends IService<BrandEntity> {

    /**
     * 累出所有品牌信息
     * @param params 品牌信息
     * @return PageUtils
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 级联更新
     * @param brand 品牌
     */
    void updateDetail(BrandEntity brand);

    /**
     * 查询品牌信息
     * @param brandIds 所有品牌id
     * @return 品牌信息
     */
    List<BrandVo> getBrandsByIds(List<Long> brandIds);
}

