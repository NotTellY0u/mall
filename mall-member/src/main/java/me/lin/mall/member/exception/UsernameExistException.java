package me.lin.mall.member.exception;

/**
 * @Author Fibonacci
 * @Date 2021/3/29 2:35 下午
 * @Version 1.0
 */
public class UsernameExistException extends RuntimeException {

    public UsernameExistException() {
        super("用户名存在");
    }
}
