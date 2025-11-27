package com.huangjinbin.back_end_src_code.dto.response;

import lombok.Data;

/**
 * 数据概览响应DTO（接口返回对象名为dataView）
 */
@Data
public class DataViewResponseDTO {
    private Long totalTasks; // 总任务数（修改为Long）
    private Long inProgressTasks; // 进行中任务数（修改为Long）
    private Long completedTasks; // 已完成任务数（修改为Long）
    private Long totalParticipants; // 总参与人数（修改为Long）
}