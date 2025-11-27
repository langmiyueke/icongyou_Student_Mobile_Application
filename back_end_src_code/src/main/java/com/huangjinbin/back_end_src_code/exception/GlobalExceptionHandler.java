package com.huangjinbin.back_end_src_code.exception;

import com.huangjinbin.back_end_src_code.dto.response.ResultDTO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// 注意：Spring Boot 3.x使用jakarta.validation，而非javax.validation
import jakarta.validation.ConstraintViolationException;

/**
 * 全局异常处理器（统一处理所有Controller层抛出的异常）
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常（如任务不存在、非团队任务等）
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResultDTO<?> handleBusinessException(BusinessException e) {
        return ResultDTO.fail(e.getMessage());
    }

    /**
     * 处理参数校验异常（如@NotNull注解触发的异常）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResultDTO<?> handleConstraintViolationException(ConstraintViolationException e) {
        // 获取第一个错误信息返回（避免多参数错误时信息过长）
        String message = e.getConstraintViolations().iterator().next().getMessage();
        return ResultDTO.fail(message);
    }

    /**
     * 处理其他未知异常（如数据库异常、代码bug等）
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultDTO<?> handleException(Exception e) {
        // 生产环境建议记录日志（如使用SLF4J），此处简化处理
        e.printStackTrace(); // 开发环境打印堆栈信息
        return ResultDTO.error("服务器内部错误：" + e.getMessage());
    }


}