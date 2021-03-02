package me.lin.mall.search.controller;

import me.lin.mall.search.service.MallSearchService;
import me.lin.mall.search.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Fibonacci
 * @create: 2021-03-02 15:58
 * @Version 1.0
 */
@Controller
public class SearchController {

    final
    MallSearchService mallSearchService;

    public SearchController(MallSearchService mallSearchService) {
        this.mallSearchService = mallSearchService;
    }

    @GetMapping("/list.html")
    public String listPage(SearchParam param){

        mallSearchService.search(param);
        return "list";
    }

}
