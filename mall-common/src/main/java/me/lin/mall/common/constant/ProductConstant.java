package me.lin.mall.common.constant;

/**
 * @Author Fibonacci
 * @create: 2021-01-14 17:18
 * @Version 1.0
 */
public class ProductConstant {
    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),
        ATTR_TYPE_SALE(0,"销售属性");

        AttrEnum(int code,String msg){
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
