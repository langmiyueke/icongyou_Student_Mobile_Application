package com.huangjinbin.back_end_src_code.exception;

/**
 * 自定义业务异常（用于业务逻辑错误，如任务不存在、参数无效等）
 */
public class BusinessException extends RuntimeException {

    // 异常状态码（可选，用于前端区分错误类型）
    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400; // 默认参数错误码
    }

    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    // getter
    public Integer getCode() {
        return code;
    }
}