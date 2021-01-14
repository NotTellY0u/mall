package me.lin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.product.entity.AttrEntity;
import me.lin.mall.product.vo.AttrRespVo;
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

    /**
     * 分页查询规格参数
     * @param params 查询条件
     * @param catelogId 三级分类id
     * @param type 属性类型
     * @return 规格参数
     */
    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    /**
     * 查询属性详细信息
     * @param attrId 属性id
     * @return
     */
    AttrRespVo getAttrInfo(Long attrId);
    /**
     * 更新页面属性详细信息
     * @param attr 属性id
     * @return
     */
    void updateAttr(AttrVo attr);
}

