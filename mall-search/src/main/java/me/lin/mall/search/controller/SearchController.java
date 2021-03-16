package me.lin.mall.search.controller;

import me.lin.mall.search.service.MallSearchService;
import me.lin.mall.search.vo.SearchParam;
import me.lin.mall.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

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
    public String listPage(SearchParam param, Model model, HttpServletRequest request){

        String queryString = request.getQueryString();
        param.set_queryString(queryString);
        // 1.根据传递来的页面的查询参数，去es中检索商品
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result",result);
            return "list";
    }

}
