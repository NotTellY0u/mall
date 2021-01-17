package me.lin.mall.product.controller;

import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.R;
import me.lin.mall.product.entity.AttrEntity;
import me.lin.mall.product.entity.AttrGroupEntity;
import me.lin.mall.product.service.AttrAttrgroupRelationService;
import me.lin.mall.product.service.AttrGroupService;
import me.lin.mall.product.service.AttrService;
import me.lin.mall.product.service.CategoryService;
import me.lin.mall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    AttrService attrService;

    @Autowired
    AttrAttrgroupRelationService relationService;
    
    /**
     * 新增分组与属性关联
     * @param vos 新增的数据
     * @return R 关联结果
     */
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos){
        relationService.saveBatch(vos);
        return R.ok();
    }

    /**
     * 删除属性和分类关联
     *
     * @param vos 属性id、分类id
     * @return 删除成功信息
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos) {
        attrService.deleteRelation(vos);
        return R.ok();

    }

    /**
     * 通过分组id查询分组关联的属性信息
     *
     * @param attrGroupId 分组id
     * @return 属性信息
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrGroupId) {
        List<AttrEntity> entities = attrService.getRelationAttr(attrGroupId);
        return R.ok().put("data", entities);
    }

    /**
     * 通过分组id查询当前分组未关联的属性信息
     * @param attrGroupId 分组id
     * @param params 属性信息
     * @return R
     */
    @GetMapping("/{attrgroupid}/noattr/relation")
    public R attrNotRelation(@PathVariable("attrgroupid") Long attrGroupId,
                             @RequestParam Map<String, Object> params) {
        PageUtils pageUtils = attrService.getNotRelationAttr(params,attrGroupId);
        return R.ok().put("data", pageUtils);

    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
