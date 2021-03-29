package me.lin.mall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.lin.mall.member.entity.MemberEntity;
import me.lin.mall.member.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 22:34:38
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    /**
     * @return 返回默认等级
     */
    MemberEntity getDeafaultLevel();
}
