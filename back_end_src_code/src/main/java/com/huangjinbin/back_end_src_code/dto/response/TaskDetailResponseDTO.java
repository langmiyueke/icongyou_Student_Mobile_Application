package com.huangjinbin.back_end_src_code.dto.response;

import lombok.Data;

@Data
public class TaskDetailResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String deadline; // 直接存储格式化后的字符串
    private String createTime; // 直接存储格式化后的字符串
    private String status;
    private Integer participants;
    private Integer difficulty;

}