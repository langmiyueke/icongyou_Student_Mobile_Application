package com.huangjinbin.back_end_src_code.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * 任务实体：对应数据库 task 表
 */
@Data
@TableName("task") // 与数据库表名完全一致
public class Task {
    /**
     * 任务ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联课程ID（对应 course 表主键）
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 任务名称
     */
    @TableField("name")
    private String name;

    /**
     * 任务描述
     */
    @TableField("description")
    private String description;

    /**
     * 任务开始时间
     */
    @TableField("start_time")
    private Long startTime;

    /**
     * 任务结束时间（截止时间）
     */
    @TableField("end_time")
    private Long endTime;

    /**
     * 是否团队任务（0：个人任务；1：团队任务）
     */
    @TableField("is_team_task")
    private Integer isTeamTask;


    public Integer getIsTeamTask() { return isTeamTask; }



    /**
     * 是否必做任务（0：选做；1：必做）
     */
    @TableField("is_must")
    private Boolean isMust;

    /**
     * 任务积分
     */
    @TableField("points")
    private Integer points;

    /**
     * 任务难度等级（1-简单；2-中等；3-困难）
     */
    @TableField("level")
    private Integer level;

    /**
     * 锁定状态（0：未锁定；1：已锁定）
     */
    @TableField("lock_status")
    private Integer lockStatus;

    /**
     * 可提交次数（-1：无限制）
     */
    @TableField("times")
    private Integer times;

    // 在Task类中添加
    @TableField("max_participants")
    private Integer maxParticipants; // 最大参与人数


}
