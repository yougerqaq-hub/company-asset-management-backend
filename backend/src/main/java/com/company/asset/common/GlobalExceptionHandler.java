package com.company.asset.common;

import org.springframework.web.bind.annotation.*;

/**
 * 将业务和参数异常转换为统一 Result，便于前端统一处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result<Void> business(BusinessException e) {
        return Result.fail(400, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> error(Exception e) {
        return Result.fail(500, "服务器异常：" + e.getMessage());
    }
}
