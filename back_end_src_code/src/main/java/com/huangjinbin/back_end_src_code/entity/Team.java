package com.huangjinbin.back_end_src_code.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 团队实体：对应数据库 team 表
 */
@Data
@TableName("team")
public class Team {
    /**
     * 团队ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 团队名称
     */
    private String name;

    /**
     * 团队负责人ID（关联 user 表主键）
     */
    @TableField("leader_id")  // 确保字段名为leader_id
    private Long leaderId;

    /**
     * 团队描述
     */
    private String description;

    /**
     * 团队创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 团队状态（0：解散；1：正常）
     */
    private Integer status;
}