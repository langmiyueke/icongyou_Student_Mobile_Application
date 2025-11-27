package com.huangjinbin.back_end_src_code.dto.response;

import lombok.Data;

/**
 * 团队进度响应DTO（接口返回的map结构）
 */
@Data
public class TeamProcessResponseDTO {
    private Integer teamProgress; // 团队进度
    private Integer totalTasks; // 总任务数
    private Integer completedTasks; // 已完成任务数
    private Integer inProgressTasks; // 进行中任务数
    private Integer pendingTasks; // 待处理任务数
}