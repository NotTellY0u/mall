package me.lin.mall.member.dao;

import me.lin.mall.member.entity.MemberStatisticsInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员统计信息
 * 
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 22:34:38
 */
@Mapper
public interface MemberStatisticsInfoDao extends BaseMapper<MemberStatisticsInfoEntity> {
	
}
