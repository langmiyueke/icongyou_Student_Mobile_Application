package com.huangjinbin.back_end_src_code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huangjinbin.back_end_src_code.entity.TaskSubmissionHeat;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 任务热度表Mapper接口（辅助热点数据查询）
 */
public interface TaskSubmissionHeatMapper extends BaseMapper<TaskSubmissionHeat> {

    /**
     * 根据任务ID查询热度值（用于任务列表的heat字段）
     */
    @Select("SELECT heat_index FROM task_submission_heat WHERE task_id = #{taskId}")
    Integer selectHeatByTaskId(@Param("taskId") Long taskId);
}