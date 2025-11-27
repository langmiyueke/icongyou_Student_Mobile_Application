package com.huangjinbin.back_end_src_code.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 统计工具类（处理百分比计算、数据聚合等）
 */
public class StatUtils {

    /**
     * 计算提交率（保留2位小数，返回百分比字符串）
     * @param submitted 已提交数量
     * @param total 总数量
     * @return 提交率（如"68.50%"）
     */
    public static String calculateSubmitRate(int submitted, int total) {
        if (total == 0) {
            return "0.00%";
        }
        // 计算百分比并保留2位小数
        BigDecimal rate = new BigDecimal(submitted)
                .divide(new BigDecimal(total), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        return rate.setScale(2, RoundingMode.HALF_UP) + "%";
    }

    /**
     * 计算团队进度（已完成任务数/总任务数，返回整数百分比）
     * @param completed 已完成任务数
     * @param total 总任务数
     * @return 进度百分比（如60）
     */
    public static int calculateTeamProgress(int completed, int total) {
        if (total == 0) {
            return 0;
        }
        return (int) (new BigDecimal(completed)
                .divide(new BigDecimal(total), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .doubleValue());
    }
}