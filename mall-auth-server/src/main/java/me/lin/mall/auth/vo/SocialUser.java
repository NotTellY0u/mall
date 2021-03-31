package me.lin.mall.auth.vo;

import lombok.Data;

/**
 * @Author Fibonacci
 * @Date 2021/3/30 3:21 下午
 * @Version 1.0
 */
@Data
public class SocialUser {

    private String accessToken;

    private String remindIn;

    private long expiresIn;

    private String uId;

    private String isRealName;
}
