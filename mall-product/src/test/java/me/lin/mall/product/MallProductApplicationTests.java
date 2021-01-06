package me.lin.mall.product;

import me.lin.mall.product.entity.BrandEntity;
import me.lin.mall.product.service.BrandService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *  1. 引入oss-starter
 *  2. 配置key,endpoint相关信息
 *  3. 使用OSSClient，进行相关操作
 */
@SpringBootTest
class MallProductApplicationTests {

	@Autowired
	BrandService brandService;

	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();

		brandEntity.setName("苹果Apple");
		brandService.save(brandEntity);
		System.out.println("保存成功...");
	}

}
