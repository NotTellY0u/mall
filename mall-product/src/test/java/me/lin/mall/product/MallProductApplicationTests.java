package me.lin.mall.product;

import me.lin.mall.product.entity.BrandEntity;
import me.lin.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class MallProductApplicationTests {

	@Autowired
	BrandService brandService;

	@Test
	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();

		brandEntity.setName("苹果Apple");
		brandService.save(brandEntity);
		System.out.println("保存成功...");
	}

}
