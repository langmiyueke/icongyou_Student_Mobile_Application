package com.huangjinbin.back_end_src_code.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务热度实体：对应数据库 task_submission_heat 表
 */
@Data
@TableName("task_submission_heat")
public class TaskSubmissionHeat {
    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID（关联 task 表主键）
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 热度指数（点击量、参与量等统计值）
     */
    @TableField("heat_index")
    private Integer heatIndex;

    /**
     * 最后更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 今日新增热度
     */
    @TableField("today_heat")
    private Integer todayHeat;
}