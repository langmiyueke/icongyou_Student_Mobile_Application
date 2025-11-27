package com.huangjinbin.back_end_src_code.service.impl;

import com.huangjinbin.back_end_src_code.dto.response.TeamMemberResponseDTO;
import com.huangjinbin.back_end_src_code.dto.response.TeamProcessResponseDTO;
import com.huangjinbin.back_end_src_code.entity.Task;
import com.huangjinbin.back_end_src_code.mapper.TaskMapper;
import com.huangjinbin.back_end_src_code.mapper.TeamMemberMapper;
import com.huangjinbin.back_end_src_code.mapper.TeamProcessMapper;
import com.huangjinbin.back_end_src_code.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private TeamProcessMapper teamProcessMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public List<TeamMemberResponseDTO> getTeamMembers(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null || task.getIsTeamTask() != 1) {
            throw new RuntimeException("该任务不是团队任务，无团队成员");
        }
        return teamMemberMapper.selectTeamMembersByTaskId(taskId);
    }

    @Override
    public TeamProcessResponseDTO getTeamProcess(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        // 从Mapper获取统计数据
        Map<String, Integer> processMap = teamProcessMapper.selectTeamProcessByTaskId(taskId);

        // 封装到DTO
        TeamProcessResponseDTO responseDTO = new TeamProcessResponseDTO();
        responseDTO.setTeamProgress(processMap.getOrDefault("teamProgress", 0));
        responseDTO.setTotalTasks(processMap.getOrDefault("totalTasks", 0));
        responseDTO.setCompletedTasks(processMap.getOrDefault("completedTasks", 0));
        responseDTO.setInProgressTasks(processMap.getOrDefault("inProgressTasks", 0));
        responseDTO.setPendingTasks(processMap.getOrDefault("pendingTasks", 0));

        return responseDTO;
    }
}