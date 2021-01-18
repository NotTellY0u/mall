package me.lin.mall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import me.lin.mall.product.entity.AttrEntity;

import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-01-18 13:46
 * @Version 1.0
 */
@Data
public class AttrGroupWithAttrsVo {
    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
