package me.lin.mall.product;

import com.aliyun.oss.OSSClient;
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

	@Autowired
	OSSClient ossClient;

	@Test
	public void testUpload() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("C:\\Users\\yangf\\Pictures\\1.jpg");
		ossClient.putObject("mall-hello891", "3.jpg", fileInputStream);
		ossClient.shutdown();
		System.out.println("上传完成");
	}

	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();

		brandEntity.setName("苹果Apple");
		brandService.save(brandEntity);
		System.out.println("保存成功...");
	}

}
