package me.lin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.product.entity.AttrGroupEntity;
import me.lin.mall.product.vo.AttrGroupWithAttrsVo;
import me.lin.mall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
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
     * 分页查询功能
     * @param params 分页参数
     * @return PageUtils
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 分页查询功能
     * @param params 分页参数
     * @param catelogId 分类id
     * @return pageUtils
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    /**
     * 获取分类下所有分组及关联属性
     * @param catelogId 分类id
     * @return
     */
    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    /**
     * 属性列表
     * @param spuId spuId
     * @param catalogId
     * @return 属性列表
     */
    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

