package me.lin.mall.product.vo;

import lombok.Data;
import me.lin.mall.product.vo.Attr;
import me.lin.mall.product.vo.MemberPrice;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Skus {

    private List<Attr> attr;

    private String skuName;

    private BigDecimal price;

    private String skuTitle;

    private String skuSubtitle;

    private List<Images> images;

    private List<String> descar;

    private int fullCount;

    private BigDecimal discount;

    private int countStatus;

	/**
	 * 满减价格
	 */
	private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private int priceStatus;

    private List<MemberPrice> memberPrice;
}