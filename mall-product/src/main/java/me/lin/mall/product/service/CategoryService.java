package me.lin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.product.entity.CategoryEntity;
import me.lin.mall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    /**
     * 找到catelogId的完整路径
     * 【一层/二层/三层】
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    /**
     * 级联更新分类
     * @param category 分类名
     */
    void updateCascade(CategoryEntity category);

    /**
     * 获取一级分类
     * @return 一级分类列表
     */
    List<CategoryEntity> getLevelOneCategorys();

    /**
     * 获取三级分类菜单
     * @return 三级分类json
     */
    Map<String, List<Catelog2Vo>> getCatalogJson();
}

