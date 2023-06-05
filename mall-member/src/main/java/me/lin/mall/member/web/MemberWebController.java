package me.lin.mall.member.web;

import me.lin.mall.common.utils.R;
import me.lin.mall.member.feign.OrderFeignService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
public class MemberWebController {

    @Resource
    OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                  Model model, HttpServletRequest request){
        //获取到支付宝传过来的所有请求数据
        // request验证签名，如果正确可以修改
        HashMap<String, Object> page = new HashMap<>();
        page.put("page",pageNum.toString());
        R r = orderFeignService.listWithItem(page);
        // 查出当前登录用户所有订单列表数据
        model.addAttribute("orders",r);
        return "orderList";
    }

}
