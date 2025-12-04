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
    @Select("""
SELECT 
    t.id,
    t.name AS title,
    t.description,
    CASE WHEN t.is_team_task = 1 THEN '团队' ELSE '个人' END AS type,
    DATE_FORMAT(FROM_UNIXTIME(t.end_time / 1000), '%Y-%m-%d') AS deadline,
    COALESCE(th.submitted_count, 0) AS participants,
    COALESCE(th.heat_index, 0) AS heat
FROM task_g t
LEFT JOIN task_submission_heat th ON t.id = th.task_id
""")
    List<TaskListResponseDTO> selectTaskList();

    /**
     * 查询数据概览统计
     */
    @Select("""
SELECT 
    COUNT(DISTINCT ps.task_id) AS totalTasks,
    COUNT(DISTINCT CASE WHEN ps.status = 0 THEN ps.task_id END) AS inProgressTasks,
    COUNT(DISTINCT CASE WHEN ps.status = 1 THEN ps.task_id END) AS completedTasks,
    COUNT(DISTINCT ps.user_id) AS totalParticipants
FROM personal_submit_g ps
JOIN task_g t ON ps.task_id = t.id
JOIN user_g u ON ps.user_id = u.id
WHERE t.is_team_task = 0
""")
    Map<String, Object> selectDataViewStat();

    /**
     * 查询热度数据统计
     */
    @Select("""
SELECT 
    COALESCE(SUM(th.today_heat), 0) AS today,
    COALESCE(SUM(CASE 
        WHEN th.create_time BETWEEN DATE_SUB(CURDATE(), INTERVAL 6 DAY) 
                                AND DATE_ADD(CURDATE(), INTERVAL 1 DAY) 
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
        COALESCE((SELECT COUNT(DISTINCT ps.user_id) FROM personal_submit_g ps WHERE ps.task_id = t.id), 0) +
        COALESCE((SELECT COUNT(DISTINCT tm.user_id) FROM team_member_g tm JOIN team_g t2 ON tm.team_id = t2.id WHERE t2.course_id = t.course_id), 0)
    ) AS participants,
    t.level AS difficulty,
    CASE 
        WHEN t.end_time <= UNIX_TIMESTAMP() THEN '已结束'
        WHEN t.start_time > UNIX_TIMESTAMP() THEN '未开始'
        ELSE '进行中' 
    END AS status
FROM task_g t
LEFT JOIN course_g c ON t.course_id = c.id
WHERE t.id = #{taskId}
""")
    TaskDetailResponseDTO selectTaskDetailById(@Param("taskId") Long taskId);

    /**
     * 查询团队任务进度统计
     */
    @Select("""
SELECT 
    CAST(AVG(tm.progress) AS UNSIGNED) AS teamProgress,
    COUNT(DISTINCT t.id) AS totalTasks,
    SUM(CASE WHEN t.end_time <= UNIX_TIMESTAMP() THEN 1 ELSE 0 END) AS completedTasks,
    SUM(CASE WHEN t.start_time <= UNIX_TIMESTAMP() AND t.end_time > UNIX_TIMESTAMP() THEN 1 ELSE 0 END) AS inProgressTasks,
    SUM(CASE WHEN t.start_time > UNIX_TIMESTAMP() THEN 1 ELSE 0 END) AS pendingTasks
FROM task_g t
JOIN course_g c ON t.course_id = c.id
JOIN team_g team ON c.id = team.course_id
JOIN team_member_g tm ON team.id = tm.team_id
WHERE t.id = #{id} 
  AND t.is_team_task = 1
  AND team.status = 1
GROUP BY team.id
""")
    Map<String, Object> selectTeamProcess(@Param("id") Long id);

}
