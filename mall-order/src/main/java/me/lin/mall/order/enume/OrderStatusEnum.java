package me.lin.mall.order.enume;

/**
 * @author Fibonacci
 */
public enum OrderStatusEnum {

    // 待付款
    CREATE_NEW(0,"待付款"),
    // 已付款
    PAYED(1,"已付款"),
    // 已发货
    SENDED(2,"已发货"),
    // 已完成
    RECIEVED(3,"已完成"),
    // 已取消
    CANCLED(4,"已取消"),
    // 售后中
    SERVICING(5,"售后中"),
    // 售后完成
    SERVICED(6,"售后完成");


    private String msg;
    private Integer code;

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }

    OrderStatusEnum(Integer code, String msg){
        this.msg = msg;
        this.code = code;
    }
}