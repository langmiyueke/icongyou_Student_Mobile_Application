package com.huangjinbin.back_end_src_code.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
@Data
public class TaskCreateRequestDTO {
    @NotBlank(message = "任务标题不能为空")
    private String title; // 对应Task实体的name字段

    @NotBlank(message = "任务类型不能为空")
    private String type; // 个人/团队，需转换为Task实体的isTeamTask（0/1）

    private String description; // 任务描述

    @NotBlank(message = "截止时间不能为空")
    private String deadline; // 前端传递的日期字符串，需转换为时间戳（Task的endTime）

    @NotNull(message = "难度等级不能为空")
    private Integer difficulty; // 对应Task实体的level（1-3）

    private Integer maxParticipants; // 可根据业务需求决定是否关联到Task实体（当前Task实体无对应字段，可暂存或扩展实体）
}