package com.huangjinbin.back_end_src_code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huangjinbin.back_end_src_code.entity.Task;
import com.huangjinbin.back_end_src_code.dto.response.TaskDetailResponseDTO;
import com.huangjinbin.back_end_src_code.dto.response.TaskListResponseDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 查询任务列表数据
     */
// 在TaskMapper.java中修改selectTaskList方法的SQL
    @Select("""
SELECT 
    t.id,
    t.name AS title,
    t.description,
    CASE WHEN t.is_team_task = 1 THEN '团队' ELSE '个人' END AS type,
    DATE_FORMAT(FROM_UNIXTIME(t.end_time / 1000), '%Y-%m-%d') AS deadline,
    COALESCE(th.submitted_count, 0) AS participants,
    COALESCE(th.heat_index, 0) AS heat
FROM Task t
LEFT JOIN Task_Submission_Heat th ON t.id = th.task_id
""")
    List<TaskListResponseDTO> selectTaskList();

    /**
     * 查询数据概览统计
     */
    @Select("""
SELECT 
    -- 仅统计有个人提交记录的任务数
    COUNT(DISTINCT ps.task_id) AS totalTasks,
    -- 进行中：status=0（待审核）的提交对应的任务数
    COUNT(DISTINCT CASE WHEN ps.status = 0 THEN ps.task_id END) AS inProgressTasks,
    -- 已完成：status=1（通过）的提交对应的任务数
    COUNT(DISTINCT CASE WHEN ps.status = 1 THEN ps.task_id END) AS completedTasks,
    -- 去重统计提交用户数
    COUNT(DISTINCT ps.user_id) AS totalParticipants
FROM personal_submit ps
-- 关联任务表，确保只统计存在的任务
JOIN Task t ON ps.task_id = t.id
WHERE t.is_team_task = 0  -- 仅统计个人任务
""")
    Map<String, Object> selectDataViewStat();

    /**
     * 查询热度数据统计
     */
    @Select("""
SELECT 
    COALESCE(SUM(th.today_heat), 0) AS today,
    -- 核心修改：结束时间改为明天0点，包含今日所有时间
    COALESCE(SUM(CASE 
        WHEN th.create_time BETWEEN DATE_SUB(CURDATE(), INTERVAL 6 DAY) 
                                AND DATE_ADD(CURDATE(), INTERVAL 1 DAY)  -- 改为次日0点
        THEN th.heat_index 
        ELSE 0 
    END), 0) AS week,
    COALESCE(SUM(th.heat_index), 0) AS total
FROM task_submission_heat th
""")
    Map<String, Object> selectHeatDataStat();

    /**
     * 查询任务详情
     */
    @Select("""
SELECT 
    t.id,
    t.name AS title,
    t.description,
    CASE WHEN t.is_team_task = 1 THEN '团队' ELSE '个人' END AS type,
    DATE_FORMAT(FROM_UNIXTIME(t.end_time), '%Y-%m-%d') AS deadline,
    DATE_FORMAT(FROM_UNIXTIME(c.create_time), '%Y-%m-%d') AS createTime,
    (
        COALESCE((SELECT COUNT(DISTINCT ps.user_id) FROM Personal_Submit ps WHERE ps.task_id = t.id), 0) +
        COALESCE((SELECT COUNT(DISTINCT tm.user_id) FROM Team_Member tm JOIN Team t2 ON tm.team_id = t2.id WHERE t2.course_id = t.course_id), 0)
    ) AS participants,
    t.level AS difficulty,
    CASE 
        WHEN t.end_time <= UNIX_TIMESTAMP() THEN '已结束'
        WHEN t.start_time > UNIX_TIMESTAMP() THEN '未开始'
        ELSE '进行中' 
    END AS status
FROM Task t
LEFT JOIN Course c ON t.course_id = c.id
WHERE t.id = #{taskId}
""")
    TaskDetailResponseDTO selectTaskDetailById(@Param("taskId") Long taskId);

// 在 TaskMapper.java 中替换为以下代码（完全移除 CAST 转换）
    /**
     * 查询团队任务进度统计（无语法错误版本）
     */
    @Select("""
SELECT 
    -- 团队进度：使用成员平均进度（从team_member表获取）
    CAST(AVG(tm.progress) AS UNSIGNED) AS teamProgress,
    -- 总任务数：当前课程下的团队任务总数
    COUNT(DISTINCT t.id) AS totalTasks,
    -- 已完成任务数：截止时间<=当前时间
    SUM(CASE WHEN t.end_time <= UNIX_TIMESTAMP() THEN 1 ELSE 0 END) AS completedTasks,
    -- 进行中任务数：开始时间<=当前时间 且 截止时间>当前时间
    SUM(CASE WHEN t.start_time <= UNIX_TIMESTAMP() AND t.end_time > UNIX_TIMESTAMP() THEN 1 ELSE 0 END) AS inProgressTasks,
    -- 待处理任务数：开始时间>当前时间
    SUM(CASE WHEN t.start_time > UNIX_TIMESTAMP() THEN 1 ELSE 0 END) AS pendingTasks
FROM Task t
-- 关联课程、团队、团队成员（确保统计的是同一团队的任务）
JOIN Course c ON t.course_id = c.id
JOIN Team team ON c.id = team.course_id
JOIN Team_Member tm ON team.id = tm.team_id
WHERE t.id = #{id} 
  AND t.is_team_task = 1  -- 确保是团队任务
  AND team.status = 1     -- 团队状态正常
GROUP BY team.id  -- 按团队分组统计
""")
    Map<String, Object> selectTeamProcess(@Param("id") Long id);
}