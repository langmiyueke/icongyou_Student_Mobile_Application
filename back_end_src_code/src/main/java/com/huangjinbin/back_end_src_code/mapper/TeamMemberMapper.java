package com.huangjinbin.back_end_src_code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huangjinbin.back_end_src_code.dto.response.TeamMemberResponseDTO;
import com.huangjinbin.back_end_src_code.entity.TeamMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 团队成员Mapper接口，用于数据库操作
 * 继承BaseMapper<TeamMember>获得基本CRUD能力，扩展自定义查询方法
 */
public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    /**
     * 根据任务ID查询关联的团队成员信息
     * 通过任务ID关联课程、团队、团队成员和用户表，获取成员详情
     *
     * @param taskId 任务ID
     * @return 团队成员响应DTO列表
     */
    @Select("""
    SELECT 
        u.id,
        u.name,
        u.email,
        -- 将数字角色代码转换为文本描述
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
    FROM Task task
    INNER JOIN Course c ON task.course_id = c.id
    INNER JOIN Team team ON c.id = team.course_id
    INNER JOIN Team_Member tm ON team.id = tm.team_id
    INNER JOIN User u ON tm.user_id = u.id
    WHERE task.id = #{taskId}
    AND COALESCE(tm.status, 1) = 1
    AND COALESCE(team.status, 1) = 1
    """)
    List<TeamMemberResponseDTO> selectTeamMembersByTaskId(@Param("taskId") Long taskId);
}