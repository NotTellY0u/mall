package me.lin.mall.product.web;

import me.lin.mall.product.entity.CategoryEntity;
import me.lin.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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
}
