package me.lin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.product.entity.AttrEntity;
import me.lin.mall.product.vo.AttrVo;

import java.util.Map;

/**
 * 商品属性
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存分组信息
     * @param attr 要保存的信息
     */
    void saveAttr(AttrVo attr);
}

