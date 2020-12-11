package me.lin.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.common.utils.PageUtils;
import me.lin.mall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 22:34:38
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

