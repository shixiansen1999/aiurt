package com.aiurt.modules.common.api;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public interface DailyFaultApi {
    /**
     * 日待办故障数
     * @param year
     * @param month
     * @return
     */
    Map<String, Integer> getDailyFaultNum(Integer year, Integer month);

    /**
     * 获取当前用户，当天的故障维修单
     * @return
     */
    String getFaultTask();

    /**
     * 大屏班组画像维修工时统计，用户ID:维修时长
     */
    Map<String, BigDecimal> getFaultUserHours(int type, String teamId);
}
