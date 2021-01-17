package me.lin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.product.entity.BrandEntity;
import me.lin.mall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    /**
     * 分页查询
     * @param params 查询的实体类
     * @return PageUtils 分页查询结果
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存中间表的品牌和分类信息
     * @param categoryBrandRelation 中间类
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 更新品牌id、品牌名
     * @param brandId 品牌id
     * @param name 品牌名
     */
    void updateBrand(Long brandId, String name);

    /**
     * 级联更新产品id
     * @param catId 产品id
     * @param name
     */
    void updateCategory(Long catId, String name);

    /**
     * 通过分类获取品牌名
     * @param catId 产品id
     * @return List 返回所有符合条件的品牌
     */
    List<BrandEntity> getBrandsByCatId(Long catId);
}

