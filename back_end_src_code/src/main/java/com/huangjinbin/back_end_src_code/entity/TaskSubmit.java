package com.huangjinbin.back_end_src_code.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("task_submit")
public class TaskSubmit {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private Long taskId;

    @TableField("submitter_id")
    private Long submitterId;

    private String content;

    @TableField("is_excellent")
    private Integer isExcellent; // 0：否；1：是

    @TableField("submit_time")
    private LocalDateTime submitTime;
}