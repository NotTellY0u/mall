package me.lin.mall.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.lin.mall.coupon.entity.CouponEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 18:43:22
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
