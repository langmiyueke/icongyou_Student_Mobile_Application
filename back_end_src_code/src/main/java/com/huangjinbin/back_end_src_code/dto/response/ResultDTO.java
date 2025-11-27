package com.huangjinbin.back_end_src_code.dto.response;

import lombok.Data;

import java.util.Map;

/**
 * 统一响应DTO
 */
@Data
public class ResultDTO<T> {
    private int code;
    private String msg;
    private T data;

    // 成功响应（带数据键名）
    public static <T> ResultDTO<T> success(String key, T data) {
        ResultDTO<T> result = new ResultDTO<>();
        result.setCode(200);
        result.setMsg("操作成功");
        // 封装为 {key: data} 格式
        result.setData((T) Map.of(key, data));
        return result;
    }

    // 成功响应（直接返回Map）
    public static <T> ResultDTO<T> success(T data) {
        ResultDTO<T> result = new ResultDTO<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 失败响应
    public static <T> ResultDTO<T> fail(String msg) {
        ResultDTO<T> result = new ResultDTO<>();
        result.setCode(400);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    // 错误响应
    public static <T> ResultDTO<T> error(String msg) {
        ResultDTO<T> result = new ResultDTO<>();
        result.setCode(500);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}