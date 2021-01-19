package me.lin.mall.common.constant;

/**
 * @Author Fibonacci
 * @create: 2021-01-19 16:09
 * @Version 1.0
 */
public class WareConstant {
    public enum PurchaseStatusEnum{
        //新建
        CREATED(0,"新建"),
        //已分配
        ASSIGNED(1,"已分配"),
        //已领取
        RECEIVE(2,"已领取"),
        //已完成
        FINISH(3,"已完成"),
        //有异常
        HAS_ERROR(4,"有异常");

        PurchaseStatusEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }
        private int code;
        private String msg;
        public int getCode(){
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    public enum PurchaseDetailStatusEnum{
        //新建
        CREATED(0,"新建"),
        //已分配
        ASSIGNED(1,"已分配"),
        //正在采购
        BUYING(2,"正在采购"),
        //已完成
        FINISH(3,"已完成"),
        //采购失败
        HAS_ERROR(4,"采购失败");

        PurchaseDetailStatusEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }
        private int code;
        private String msg;
        public int getCode(){
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
