package me.lin.mall.product.web;

import me.lin.mall.product.service.SkuInfoService;
import me.lin.mall.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    public String skuItem(@PathVariable("skuId") Long skuId, Model model){
        System.out.println("准备查询"+skuId+"详情");
        SkuItemVo vo = skuInfoService.item(skuId);
        model.addAttribute("item", vo);
        return "item";
    }
}
