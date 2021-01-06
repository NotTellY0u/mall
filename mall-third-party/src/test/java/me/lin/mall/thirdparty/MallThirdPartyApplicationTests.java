package me.lin.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
class MallThirdPartyApplicationTests {

    @Autowired
    OSSClient ossClient;

    @Test
    public void testUpload() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\yangf\\Pictures\\1.jpg");
        ossClient.putObject("mall-hello891", "4.jpg", fileInputStream);
        ossClient.shutdown();
        System.out.println("上传完成");
    }

    @Test
    void contextLoads() {
    }

}
