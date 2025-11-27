package com.huangjinbin.back_end_src_code.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 个人任务提交实体：对应数据库 personal_submit 表
 */
@Data
@TableName("personal_submit")
public class PersonalSubmit {
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
     * 提交用户ID（关联 user 表主键）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 提交内容（如链接、文本描述）
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
     * 本次提交积分（审核通过后赋值）
     */
    private Integer points;
}