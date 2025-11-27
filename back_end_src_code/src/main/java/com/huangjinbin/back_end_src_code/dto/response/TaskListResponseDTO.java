package com.huangjinbin.back_end_src_code.dto.response;

import lombok.Data;
import java.util.Date;

@Data
public class TaskListResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String type; // 个人/团队
    // 移除原deadline字段，保留转换后的字符串字段并命名为deadline
    private String deadline; // 直接使用转换后的yyyy-MM-dd字符串
    private Integer participants; // 参与人数
    private Integer heat; // 热度（提交数+查看数+讨论数）
}