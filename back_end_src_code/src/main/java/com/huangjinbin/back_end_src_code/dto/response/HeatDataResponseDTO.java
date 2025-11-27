package com.huangjinbin.back_end_src_code.dto.response;

import lombok.Data;

/**
 * 热点数据响应DTO（接口返回对象名为heatData）
 */
@Data
public class HeatDataResponseDTO {
    private Long today; // 今日热度
    private Long week; // 本周热度
    private Long total; // 总热度
}