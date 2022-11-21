package me.lin.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.lin.mall.product.entity.BrandEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌
 * 
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {
	
}
