package com.huangjinbin.back_end_src_code.dto.response;

import lombok.Data;

/**
 * 团队成员响应DTO（接口返回对象名为teamMembers）
 */
@Data
public class TeamMemberResponseDTO {
    private Long id; // 成员ID
    private String name; // 成员姓名
    private String email; // 成员邮箱
    private String role; // 成员角色（如"测试"）
    private String avatar; // 头像URL
    private Integer progress; // 进度（百分比）
    private String status; // 状态（如"在线"）
}