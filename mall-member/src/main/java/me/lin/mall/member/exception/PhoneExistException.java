package me.lin.mall.member.exception;

/**
 * @Author Fibonacci
 * @Date 2021/3/29 2:36 下午
 * @Version 1.0
 */
public class PhoneExistException extends RuntimeException {

    public PhoneExistException() {
        super("手机号存在");
    }
}
