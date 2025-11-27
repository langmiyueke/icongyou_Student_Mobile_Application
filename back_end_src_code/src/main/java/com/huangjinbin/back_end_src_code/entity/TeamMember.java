package com.huangjinbin.back_end_src_code.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 团队成员实体：对应数据库 team_member 表（团队与用户的关联表）
 */
@Data
@TableName("team_member")
public class TeamMember {
    /**
     * 关联ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 团队ID（关联 team 表主键）
     */
    @TableField("team_id")
    private Long teamId;

    /**
     * 用户ID（关联 user 表主键）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 成员角色（0：普通成员；1：副队长；2：队长）
     */
    private Integer role;

    /**
     * 加入时间
     */
    @TableField("join_time")
    private LocalDateTime joinTime;

    /**
     * 成员状态（0：退出；1：正常）
     */
    private Integer status;

    /**
     * 成员个人进度（百分比）
     */
    private Integer progress;
}