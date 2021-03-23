package me.lin.mall.product;

import me.lin.mall.product.dao.AttrGroupDao;
import me.lin.mall.product.dao.SkuSaleAttrValueDao;
import me.lin.mall.product.entity.BrandEntity;
import me.lin.mall.product.service.BrandService;

import me.lin.mall.product.vo.SkuItemSaleAttrVo;
import me.lin.mall.product.vo.SkuItemVo;
import me.lin.mall.product.vo.SpuItemAttrGroupVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 *  1. 引入oss-starter
 *  2. 配置key,endpoint相关信息
 *  3. 使用OSSClient，进行相关操作
 */
@SpringBootTest
class MallProductApplicationTests {

	@Autowired
	AttrGroupDao attrGroupDao;

	@Autowired
	SkuSaleAttrValueDao skuSaleAttrValueDao;

	@Autowired
	BrandService brandService;

	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();

		brandEntity.setName("苹果Apple");
		brandService.save(brandEntity);
		System.out.println("保存成功...");
	}

	@Test
	public void test(){
		List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(20L);
		System.out.println(saleAttrsBySpuId.toString());
	}

}
