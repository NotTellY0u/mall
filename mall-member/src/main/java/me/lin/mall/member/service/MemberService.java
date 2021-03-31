package me.lin.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.member.entity.MemberEntity;
import me.lin.mall.member.exception.PhoneExistException;
import me.lin.mall.member.exception.UsernameExistException;
import me.lin.mall.member.vo.MemberLoginVo;
import me.lin.mall.member.vo.MemberRegistVo;
import me.lin.mall.member.vo.SocialUser;

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

    /**
     * @param vo 登录数据
     * @return 登录信息
     */
    MemberEntity login(MemberLoginVo vo);

    /**
     * 社交登录
     *
     * @param socialUser 社交用户信息
     * @return 登录信息
     */
    MemberEntity login(SocialUser socialUser) throws Exception;
}

