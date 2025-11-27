package com.huangjinbin.back_end_src_code.service;

import com.huangjinbin.back_end_src_code.dto.response.TeamMemberResponseDTO;
import com.huangjinbin.back_end_src_code.dto.response.TeamProcessResponseDTO;

import java.util.List;

/**
 * 团队相关服务接口（对应接口文档5-6）
 */
public interface TeamService {

    /**
     * 获取团队成员列表（/task/loadTeamMembers）
     */
    List<TeamMemberResponseDTO> getTeamMembers(Long taskId);

    /**
     * 获取团队进度（/task/loadTeamProcess）
     * 修正：返回TeamProcessResponseDTO，与实现类和接口格式匹配
     */
    TeamProcessResponseDTO getTeamProcess(Long taskId); // 返回类型从Map改为DTO
}