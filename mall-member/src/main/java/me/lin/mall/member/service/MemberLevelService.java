package me.lin.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.common.utils.PageUtils;
import me.lin.mall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 22:34:38
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

