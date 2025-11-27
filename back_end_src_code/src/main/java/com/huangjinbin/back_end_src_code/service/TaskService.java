package com.huangjinbin.back_end_src_code.service;

import com.huangjinbin.back_end_src_code.dto.request.TaskCreateRequestDTO;
import com.huangjinbin.back_end_src_code.dto.response.TaskDetailResponseDTO;
import com.huangjinbin.back_end_src_code.dto.response.TaskListResponseDTO;
import com.huangjinbin.back_end_src_code.dto.response.TeamMemberResponseDTO;
import com.huangjinbin.back_end_src_code.dto.response.TeamProcessResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * 任务服务接口
 * 定义任务相关的业务方法
 */
public interface TaskService {

    /**
     * 加载任务列表
     * @return 任务列表DTO集合
     */
    List<TaskListResponseDTO> loadTaskList();

    /**
     * 加载数据概览统计
     * @return 包含总任务数、进行中任务数等的Map
     */
    Map<String, Long> loadDataView(); // 保持接口返回类型不变，内部处理转换

    /**
     * 加载热点数据统计
     * @return 包含今日、本周、总热度的Map
     */
    Map<String, Long> loadHeatData();

    /**
     * 加载任务详情
     * @param id 任务ID
     * @return 任务详情DTO
     */
    TaskDetailResponseDTO loadDetailTask(Long id);

    List<TeamMemberResponseDTO> loadTeamMembers(Long id);

    TeamProcessResponseDTO loadTeamProcess(Long id);

    /**
     * 新建任务
     * @param requestDTO 任务创建参数
     */
    void createTask(TaskCreateRequestDTO requestDTO);
}