package me.lin.mall.product.web;

import me.lin.mall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.server.PathParam;

/**
 * @Author Fibonacci
 * @Date 2021/3/18 1:56 下午
 * @Version 1.0
 */
@Controller
public class ItemController {

    final
    SkuInfoService skuInfoService;

    public ItemController(SkuInfoService skuInfoService) {
        this.skuInfoService = skuInfoService;
    }

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId){
        System.out.println("准备查询"+skuId+"详情");
        SkuItemVo vo = skuInfoService.item(skuId);
        return "item";
    }
}
