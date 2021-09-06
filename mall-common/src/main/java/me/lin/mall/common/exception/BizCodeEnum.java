package me.lin.mall.common.exception;

/**
 * @Author Fibonacci
 * @create: 2021-01-08 10:32
 * @Version 1.0
 */
public enum BizCodeEnum {
    UNKNOWM_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002, "验证码获取频率太高，请稍后再试"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    USER_EXIST_EXCEPTION(150001, "用户已存在"),
    PHONE_EXIST_EXCEPTION(150002, "用户已存在"),
    NO_STOCK_EXCEPTION(21000, "商品库存不足"),
    LOGINACCT_PASSWORD_INVAILD_EXCEPTION(15003, "用户名或密码错误");


    private final int code;
    private final String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
