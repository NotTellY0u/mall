package me.lin.mall.common.exception;

/**
 * @Author Fibonacci
 * @create: 2021-01-08 10:32
 * @Version 1.0
 */
public enum BizCodeEnum {
    UNKNOWM_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");



    private int code;
    private String msg;

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
