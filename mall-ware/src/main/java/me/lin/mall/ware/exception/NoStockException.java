package me.lin.mall.ware.exception;

public class NoStockException extends RuntimeException{

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    private Long skuId;

    public NoStockException(Long skuId){
        super("商品id"+skuId+"没有足够的库存了");
    }
}
