package me.lin.mall.cart.vo;

import lombok.Data;

/**
 * @Author Fibonacci
 * @Date 2021/4/13 2:36 下午
 * @Version 1.0
 */
@Data
public class UserInfoTo {

    private Long userId;

    private String userKey;

    private boolean tempUser = false;
}
