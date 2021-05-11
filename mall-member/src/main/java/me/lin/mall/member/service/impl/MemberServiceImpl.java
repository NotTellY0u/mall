package me.lin.mall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.lin.mall.common.utils.HttpUtils;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;
import me.lin.mall.member.dao.MemberDao;
import me.lin.mall.member.dao.MemberLevelDao;
import me.lin.mall.member.entity.MemberEntity;
import me.lin.mall.member.exception.PhoneExistException;
import me.lin.mall.member.exception.UsernameExistException;
import me.lin.mall.member.service.MemberService;
import me.lin.mall.member.vo.MemberLoginVo;
import me.lin.mall.member.vo.MemberRegistVo;
import me.lin.mall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
        memberEntity.setNickname(registVo.getUserName());
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

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginAcct = vo.getLoginAcct();
        String passWord = vo.getPassWord();
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginAcct)
                .or().eq("mobile", loginAcct));
        if (entity == null) {

            return null;
        } else {
            String passwordDb = entity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(passWord, passwordDb);
            if (matches) {
                return entity;
            } else {
                return null;
            }
        }
    }

    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        // 登录和注册合并逻辑
        String uId = socialUser.getUId();
        // 判断当前社交用户是否已经登陆过系统
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uId));
        if (memberEntity != null) {
            // 这个用户已经注册过
            MemberEntity memberUpdate = new MemberEntity();
            memberUpdate.setId(memberEntity.getId());
            memberUpdate.setAccessToken(socialUser.getAccessToken());
            memberUpdate.setExpiresIn(String.valueOf(socialUser.getExpiresIn()));
            this.baseMapper.updateById(memberUpdate);
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(String.valueOf(socialUser.getExpiresIn()));
            return memberEntity;
        } else {
            // 没有查到当前社交用户对应的记录我们就需要注册一个
            MemberEntity regist = new MemberEntity();
            // 查询当前社交用户的社交账号信息(昵称，性别等)
            try {
                Map<String, String> query = new HashMap<>();
                query.put("access_token", socialUser.getAccessToken());
                query.put("uid", socialUser.getUId());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String s = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(s);
                    // 昵称
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    regist.setNickname(name);
                    regist.setGender("m".equals(gender) ? 1 : 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            regist.setSocialUid(socialUser.getUId());
            regist.setAccessToken(socialUser.getAccessToken());
            regist.setExpiresIn(String.valueOf(socialUser.getExpiresIn()));
            this.baseMapper.insert(regist);
            return regist;
        }

    }

}