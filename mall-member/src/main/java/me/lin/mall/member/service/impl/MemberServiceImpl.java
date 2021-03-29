package me.lin.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;
import me.lin.mall.member.dao.MemberDao;
import me.lin.mall.member.dao.MemberLevelDao;
import me.lin.mall.member.entity.MemberEntity;
import me.lin.mall.member.exception.PhoneExistException;
import me.lin.mall.member.exception.UsernameExistException;
import me.lin.mall.member.service.MemberService;
import me.lin.mall.member.vo.MemberRegistVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    final MemberLevelDao memberLevelDao;

    public MemberServiceImpl(MemberLevelDao memberLevelDao) {
        this.memberLevelDao = memberLevelDao;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo registVo) {
        MemberEntity memberEntity = new MemberEntity();

        // 1.设置默认等级
        MemberEntity levelEntity = memberLevelDao.getDeafaultLevel();
        memberEntity.setLevelId(levelEntity.getLevelId());
        // 检查用户名和手机号是否唯一,为了让controller能感知异常，异常机制
        checkPhoneUnique(registVo.getPhone());
        checkUserNameUnique(registVo.getUserName());
        memberEntity.setMobile(registVo.getPhone());
        memberEntity.setUsername(registVo.getUserName());
        // 密码加密存储
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(registVo.getPassWord());
        memberEntity.setPassword(encode);
        baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer mobile = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (mobile > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String userName) throws UsernameExistException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (count > 0) {
            throw new UsernameExistException();
        }
    }

}