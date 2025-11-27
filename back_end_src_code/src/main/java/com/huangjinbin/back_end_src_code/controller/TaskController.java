package com.huangjinbin.back_end_src_code.controller;

import com.huangjinbin.back_end_src_code.dto.request.TaskCreateRequestDTO;
import com.huangjinbin.back_end_src_code.dto.response.*;
import com.huangjinbin.back_end_src_code.entity.Result;
import com.huangjinbin.back_end_src_code.exception.BusinessException;
import com.huangjinbin.back_end_src_code.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务相关接口控制器
 * 处理所有/task/*路径的请求，对应接口文档1-6
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 1. 任务列表接口
     * 请求路径：/task/loadTaskList
     * 返回对象：直接返回任务列表（Result.data 为 List<TaskListResponseDTO>）
     */
    @GetMapping("/loadTaskList")
    public Result<List<TaskListResponseDTO>> loadTaskList() {
        // 1. 获取任务列表数据
        List<TaskListResponseDTO> tasks = taskService.loadTaskList();

        // 2. 直接将列表作为 data 传入 Result.success()
        return Result.success(tasks);
    }

    /**
     * 2. 数据概览接口
     * 请求路径：/task/loadDataView
     * 返回对象：dataView（数据统计）
     */
    @GetMapping("/loadDataView")
    public Result<Map<String, DataViewResponseDTO>> loadDataView() {
        Map<String, Long> statMap = taskService.loadDataView();
        DataViewResponseDTO dto = new DataViewResponseDTO();
        dto.setTotalTasks(statMap.get("totalTasks"));
        dto.setInProgressTasks(statMap.get("inProgressTasks"));
        dto.setCompletedTasks(statMap.get("completedTasks"));
        dto.setTotalParticipants(statMap.get("totalParticipants"));

        // 包装为指定格式：{code:0, message:"操作成功", data:{dataView: {...}}}
        Map<String, DataViewResponseDTO> responseData = new HashMap<>();
        responseData.put("dataView", dto);
        return Result.success(responseData);
    }

    /**
     * 3. 热点数据接口
     * 请求路径：/task/loadHeatData
     * 返回对象：heatData（热度统计）
     */
    @GetMapping("/loadHeatData")
    public Result<Map<String, HeatDataResponseDTO>> loadHeatData() {
        Map<String, Long> heatMap = taskService.loadHeatData();
        HeatDataResponseDTO dto = new HeatDataResponseDTO();
        dto.setToday(heatMap.get("today"));
        dto.setWeek(heatMap.get("week"));
        dto.setTotal(heatMap.get("total"));

        // 包装为统一格式：{code:0, message:"操作成功", data:{heatData: {...}}}
        Map<String, HeatDataResponseDTO> responseData = new HashMap<>();
        responseData.put("heatData", dto);
        return Result.success(responseData);
    }
    /**
     * 4. 详细任务接口
     * 请求路径：/task/loadDetailTask
     * 参数：id（任务ID）
     * 返回对象：detailTask（任务详情）
     */
    @GetMapping("/loadDetailTask")
    public Result loadDetailTask(@RequestParam Long id) {
        TaskDetailResponseDTO detailTask = taskService.loadDetailTask(id);
        return Result.success(detailTask);
    }

    /**
     * 5. 团队成员接口
     * 请求路径：/task/loadTeamMembers
     * 参数：id（任务ID）
     * 返回对象：teamMembers（团队成员列表）
     */
    @GetMapping("/loadTeamMembers")
    public Result<Map<String, List<TeamMemberResponseDTO>>> loadTeamMembers(@RequestParam Long id) {
        List<TeamMemberResponseDTO> teamMembers = taskService.loadTeamMembers(id);
        // 包装为 {teamMembers: [...]} 结构
        Map<String, List<TeamMemberResponseDTO>> responseData = new HashMap<>();
        responseData.put("teamMembers", teamMembers);
        return Result.success(responseData);
    }
    /**
     * 6. 团队进度接口
     * 请求路径：/task/loadTeamProcess
     * 参数：id（任务ID）
     * 返回对象：process（团队进度统计，直接作为data字段的值）
     */
    @GetMapping("/loadTeamProcess")
    public Result<TeamProcessResponseDTO> loadTeamProcess(@RequestParam Long id) {
        // 1. 调用服务层获取团队进度DTO
        TeamProcessResponseDTO process = taskService.loadTeamProcess(id);

        // 2. 直接返回DTO对象，无需包装到Map中
        return Result.success(process);
    }

    /**
     * 新建任务接口
     * 请求路径：/task/newTask
     * 请求方式：POST
     */
    @PostMapping("/newTask")
    public Result<Void> newTask(@Validated @RequestBody TaskCreateRequestDTO requestDTO) {
        taskService.createTask(requestDTO);
        return Result.success(null); // 按要求返回success(null)
    }


}