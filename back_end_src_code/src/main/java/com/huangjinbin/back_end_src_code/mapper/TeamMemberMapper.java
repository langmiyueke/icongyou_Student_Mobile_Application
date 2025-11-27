package com.huangjinbin.back_end_src_code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huangjinbin.back_end_src_code.dto.response.TeamMemberResponseDTO;
import com.huangjinbin.back_end_src_code.entity.TeamMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    /**
     * 根据任务ID查询团队成员
     */
    @Select("""
    SELECT 
        u.id,
        u.name,
        u.email,
        CASE tm.role
            WHEN 1 THEN '开发'
            WHEN 2 THEN '测试' 
            WHEN 3 THEN '产品'
            WHEN 4 THEN '设计'
            WHEN 5 THEN '组长'
            ELSE '组员'
        END AS role,
        u.avatar,
        COALESCE(tm.progress, 0) AS progress,
        CASE WHEN COALESCE(u.online_status, 0) = 1 THEN '在线' ELSE '离线' END AS status
    FROM task_g task
    INNER JOIN course_g c ON task.course_id = c.id
    INNER JOIN team_g team ON c.id = team.course_id
    INNER JOIN team_member_g tm ON team.id = tm.team_id
    INNER JOIN user_g u ON tm.user_id = u.id
    WHERE task.id = #{taskId}
    AND COALESCE(tm.status, 1) = 1
    AND COALESCE(team.status, 1) = 1
    """)
    List<TeamMemberResponseDTO> selectTeamMembersByTaskId(@Param("taskId") Long taskId);
}