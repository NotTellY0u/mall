package me.lin.mall.product.exception;

import lombok.extern.slf4j.Slf4j;
import me.lin.common.exception.BizCodeEnum;
import me.lin.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * @Author Fibonacci
 * @create: 2021-01-08 09:34
 * @Version 1.0
 * 集中处理所有异常
 */
@Slf4j
@RestControllerAdvice(basePackages = "me.lin.mall.product.controller")
public class MallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题" + "e.getClass:" + e.getMessage() + e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        HashMap<String, String> map = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError -> {
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        }));
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", map);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {

        return R.error(BizCodeEnum.UNKNOWM_EXCEPTION.getCode(),BizCodeEnum.UNKNOWM_EXCEPTION.getMsg());
    }
}
