package me.lin.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import me.lin.mall.thirdparty.component.SmsComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
class MallThirdPartyApplicationTests {

    @Autowired
    OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testUpload() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("/Users/图片/QQ20190403-193346@2x.png");
        ossClient.putObject("mall-hello891", "4.jpg", fileInputStream);
        ossClient.shutdown();
        System.out.println("上传完成");
    }

    @Test
    public void testPost() {
        smsComponent.sendSmsCode("", "123456,2");
    }


    @Test
    void contextLoads() {
    }

}
