package com.huangjinbin.back_end_src_code.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.Map;

/**
 * 团队进度Mapper接口，用于查询团队任务进度统计
 */
public interface TeamProcessMapper {

    /**
     * 根据任务ID查询团队进度统计
     * 关联任务所属课程的团队，统计该团队的任务完成情况及整体进度
     */
    @Select("""
        SELECT 
            -- 团队整体进度（成员平均进度，取整数）
            CAST(AVG(tm.progress) AS UNSIGNED) AS teamProgress,
            -- 该团队在当前课程下的总任务数
            COUNT(DISTINCT t.id) AS totalTasks,
            -- 已完成任务数（截止时间<=当前时间）
            SUM(CASE WHEN t.end_time <= UNIX_TIMESTAMP() THEN 1 ELSE 0 END) AS completedTasks,
            -- 进行中任务数（开始时间<=当前时间 且 截止时间>当前时间）
            SUM(CASE WHEN t.start_time <= UNIX_TIMESTAMP() AND t.end_time > UNIX_TIMESTAMP() THEN 1 ELSE 0 END) AS inProgressTasks,
            -- 待处理任务数（开始时间>当前时间）
            SUM(CASE WHEN t.start_time > UNIX_TIMESTAMP() THEN 1 ELSE 0 END) AS pendingTasks
        FROM Task_g t  -- 原表名Task -> 改为Task_g
        -- 关联逻辑：任务->课程->团队->团队成员（确保数据属于同一团队）
        JOIN Course_g c ON t.course_id = c.id  -- 原表名Course -> 改为Course_g
        JOIN Team_g team ON c.id = team.course_id  -- 原表名Team -> 改为Team_g
        JOIN Team_Member_g tm ON team.id = tm.team_id  -- 原表名Team_Member -> 改为Team_Member_g
        WHERE t.id = #{taskId}  -- 筛选当前任务关联的团队数据
        GROUP BY team.id  -- 按团队分组，确保统计的是单个团队的进度
        """)
    Map<String, Integer> selectTeamProcessByTaskId(@Param("taskId") Long taskId);
}