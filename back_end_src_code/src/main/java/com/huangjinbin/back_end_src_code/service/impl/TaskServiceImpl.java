package com.huangjinbin.back_end_src_code.service.impl;

import com.huangjinbin.back_end_src_code.dto.request.TaskCreateRequestDTO;
import com.huangjinbin.back_end_src_code.dto.response.TaskDetailResponseDTO;
import com.huangjinbin.back_end_src_code.dto.response.TaskListResponseDTO;
import com.huangjinbin.back_end_src_code.dto.response.TeamMemberResponseDTO;
import com.huangjinbin.back_end_src_code.dto.response.TeamProcessResponseDTO;
import com.huangjinbin.back_end_src_code.entity.Task;
import com.huangjinbin.back_end_src_code.exception.BusinessException;
import com.huangjinbin.back_end_src_code.mapper.TaskMapper;
import com.huangjinbin.back_end_src_code.mapper.TeamMemberMapper;
import com.huangjinbin.back_end_src_code.mapper.TeamProcessMapper;
import com.huangjinbin.back_end_src_code.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务服务实现类
 * 基于TaskMapper提供的数据库操作，实现任务相关业务逻辑
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    // 新增：注入团队成员和团队进度的Mapper
    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private TeamProcessMapper teamProcessMapper;

    /**
     * 加载任务列表（/task/loadTaskList）
     */
    @Override
    public List<TaskListResponseDTO> loadTaskList() {
        return taskMapper.selectTaskList();
    }

    /**
     * 加载数据概览（/task/loadDataView）
     */
    @Override
    public Map<String, Long> loadDataView() {
        // 1. 查询原始统计数据（可能包含 BigInteger 类型）
        Map<String, Object> rawStatMap = taskMapper.selectDataViewStat();

        // 2. 手动转换类型为 Long
        Map<String, Long> resultMap = new HashMap<>();
        rawStatMap.forEach((key, value) -> {
            if (value instanceof BigInteger bigInteger) {
                resultMap.put(key, bigInteger.longValue()); // 关键转换
            } else if (value instanceof Number number) {
                resultMap.put(key, number.longValue()); // 兼容其他数字类型
            } else {
                resultMap.put(key, 0L); // 非数字类型默认0
            }
        });
        return resultMap;
    }
    /**
     * 加载热点数据（/task/loadHeatData）
     */
    @Override
    public Map<String, Long> loadHeatData() {
        // 1. 查询原始数据（可能包含 BigDecimal 类型）
        Map<String, Object> rawHeatMap = taskMapper.selectHeatDataStat();

        // 2. 转换为 Long 类型
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("today", convertToLong(rawHeatMap.get("today")));
        resultMap.put("week", convertToLong(rawHeatMap.get("week")));
        resultMap.put("total", convertToLong(rawHeatMap.get("total")));

        return resultMap;
    }

    // 添加数值转换工具方法（复用或新增）
    private Long convertToLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            // 支持 BigDecimal、Integer、Long 等所有数字类型
            return number.longValue();
        }
        return 0L;
    }

    /**
     * 加载详细任务（/task/loadDetailTask）
     */
    @Override
    public TaskDetailResponseDTO loadDetailTask(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在，ID: " + id); // 异常会被GlobalExceptionHandler捕获并返回统一格式
        }
        TaskDetailResponseDTO r = taskMapper.selectTaskDetailById(id);
        return r;
    }
    /**
     * 新增：实现团队成员查询方法（/task/loadTeamMembers）
     */
    @Override
    public List<TeamMemberResponseDTO> loadTeamMembers(Long taskId) {
        // 1. 先查询任务基本信息（包含是否为团队任务的标识）
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务ID不存在：" + taskId);
        }

        // 2. 判断是否为团队任务（核心逻辑：添加在这里）
        Integer isTeamTask = task.getIsTeamTask(); // 从任务对象中获取标识
        if (isTeamTask != 1) { // 假设 1=团队任务，0=个人任务
            // 非团队任务，返回空列表（或根据业务返回提示）
            return Collections.emptyList();
        }

        // 3. 只有团队任务才查询成员列表
        return teamMemberMapper.selectTeamMembersByTaskId(taskId);
    }
    /**
     * 新增：实现团队进度查询方法（/task/loadTeamProcess）
     */
    @Override
    public TeamProcessResponseDTO loadTeamProcess(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在，ID: " + id);
        }
        if (task.getIsTeamTask() != 1) {
            throw new IllegalArgumentException("该任务不是团队任务，无法查询团队进度");
        }

        Map<String, Object> statMap = taskMapper.selectTeamProcess(id);
        TeamProcessResponseDTO dto = new TeamProcessResponseDTO();
        // 使用安全转换方法，避免null导致的0值错误
        dto.setTeamProgress(convertToInteger(statMap.get("teamProgress")));
        dto.setTotalTasks(convertToInteger(statMap.get("totalTasks")));
        dto.setCompletedTasks(convertToInteger(statMap.get("completedTasks")));
        dto.setInProgressTasks(convertToInteger(statMap.get("inProgressTasks")));
        dto.setPendingTasks(convertToInteger(statMap.get("pendingTasks")));

        return dto;
    }

    // 确保转换方法正确处理null
    private Integer convertToInteger(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }

    // 已有的方法...

    /**
     * 实现新建任务逻辑
     */
    @Override
    public void createTask(TaskCreateRequestDTO requestDTO) {
        // 1. 转换DTO为实体类
        Task task = new Task();
        task.setName(requestDTO.getTitle()); // 标题映射
        task.setDescription(requestDTO.getDescription());
        task.setLevel(requestDTO.getDifficulty()); // 难度等级
        task.setMaxParticipants(requestDTO.getMaxParticipants() != null ? requestDTO.getMaxParticipants() : 10); // 默认10人

        // 2. 转换任务类型（"个人" -> 0，"团队" -> 1）
        if ("个人".equals(requestDTO.getType())) {
            task.setIsTeamTask(0);
        } else if ("团队".equals(requestDTO.getType())) {
            task.setIsTeamTask(1);
        } else {
            throw new BusinessException("任务类型只能是'个人'或'团队'");
        }

        // 3. 处理截止时间（字符串转时间戳，时区+8）
        try {
            LocalDate deadlineDate = LocalDate.parse(requestDTO.getDeadline());
            LocalDateTime deadlineTime = deadlineDate.atTime(23, 59, 59); // 当天23:59:59
            task.setEndTime(deadlineTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()); // 转换为毫秒时间戳
        } catch (DateTimeParseException e) {
            throw new BusinessException("截止时间格式错误，需为yyyy-MM-dd");
        }

        // 4. 设置默认值
        task.setStartTime(System.currentTimeMillis()); // 开始时间为当前时间
        task.setIsMust(false); // 默认为选做
        task.setPoints(0); // 默认为0积分
        task.setLockStatus(0); // 未锁定
        task.setTimes(-1); // 提交次数无限制
        // 注意：若任务需关联课程，需补充courseId（根据业务场景设置）

        // 5. 保存到数据库
        taskMapper.insert(task);
    }

}