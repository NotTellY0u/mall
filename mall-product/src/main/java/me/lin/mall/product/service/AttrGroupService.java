package me.lin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.product.entity.AttrGroupEntity;

import java.util.Map;

/**
 * 属性分组
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    /**
     *
     * @param params
     * @return PageUtils
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     *
     * @param params
     * @param catelogId
     * @return pageUtils
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);
}

