package com.huangjinbin.back_end_src_code.dto.request;

import lombok.Data;
// Spring Boot 3.x使用jakarta.validation包
import jakarta.validation.constraints.NotNull;

/**
 * 接收任务ID参数（用于查询详情、团队成员等接口）
 */
@Data
public class TaskIdRequestDTO {
    // 任务ID（不能为空，通过@NotNull校验）
    @NotNull(message = "任务ID不能为空")
    private Long id;
}