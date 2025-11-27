package com.huangjinbin.back_end_src_code.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 团队任务提交实体：对应数据库 team_submit 表
 */
@Data
@TableName("team_submit")
public class TeamSubmit {
    /**
     * 提交ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID（关联 task 表主键）
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 团队ID（关联 team 表主键）
     */
    @TableField("team_id")
    private Long teamId;

    /**
     * 提交用户ID（关联 user 表主键，提交人）
     */
    @TableField("submit_user_id")
    private Long submitUserId;

    /**
     * 提交内容
     */
    private String content;

    /**
     * 提交附件URL（多个用逗号分隔）
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 提交状态（0：待审核；1：通过；2：驳回）
     */
    private Integer status;

    /**
     * 审核意见
     */
    @TableField("review_remark")
    private String reviewRemark;

    /**
     * 提交时间
     */
    @TableField("submit_time")
    private LocalDateTime submitTime;

    /**
     * 审核时间
     */
    @TableField("review_time")
    private LocalDateTime reviewTime;

    /**
     * 团队获得积分
     */
    private Integer points;
}