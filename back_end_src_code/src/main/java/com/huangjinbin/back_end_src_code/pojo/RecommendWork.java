package com.huangjinbin.back_end_src_code.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库中Teacher_Recommended_Work表对应的实体类
 */
@TableName(value = "Teacher_Recommended_Work")
public class RecommendWork {
    @TableId
    private Integer id;
    private Integer task_id;
    private Integer work_id;
    private Integer teacher_id;
    private String comment;
    private Integer like_count;
    private Integer collect_count;

}
