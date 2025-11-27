package com.huangjinbin.back_end_src_code.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 课程实体：对应数据库 course 表（任务关联的课程）
 */
@Data
@TableName("course")
public class Course {
    /**
     * 课程ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 课程封面URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 课程创建者ID（关联 user 表主键）
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 课程状态（0：未发布；1：已发布；2：已下架）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}