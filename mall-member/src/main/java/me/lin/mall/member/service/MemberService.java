package me.lin.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.member.entity.MemberEntity;
import me.lin.mall.member.exception.PhoneExistException;
import me.lin.mall.member.exception.UsernameExistException;
import me.lin.mall.member.vo.MemberRegistVo;

import java.util.Map;

/**
 * 会员
 *
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 22:34:38
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 处理注册
     *
     * @param registVo 注册信息
     */
    void regist(MemberRegistVo registVo);


    /**
     * @param phone 手机号
     * @return 邮箱是否正确
     */
    void checkPhoneUnique(String phone) throws PhoneExistException;


    /**
     * @param userName 用户名
     * @return 用户名是否正确
     */
    void checkUserNameUnique(String userName) throws UsernameExistException;
}

