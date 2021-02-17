package me.lin.mall.product.web;

import me.lin.mall.product.entity.CategoryEntity;
import me.lin.mall.product.service.CategoryService;
import me.lin.mall.product.vo.Catalog2Vo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author Fibonacci
 * @create: 2021-02-07 10:02
 * @Version 1.0
 */
@Controller
public class IndexController {

    final CategoryService categoryService;

    public IndexController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {

        List<CategoryEntity> categoryEntityList = categoryService.getLevelOneCategorys();
        model.addAttribute("categorys",categoryEntityList);
        return "index";
    }

    @ResponseBody
    @RequestMapping("index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatlogJson() {

        Map<String, List<Catalog2Vo>> map = categoryService.getCatelogJson();
        return map;
    }

}
