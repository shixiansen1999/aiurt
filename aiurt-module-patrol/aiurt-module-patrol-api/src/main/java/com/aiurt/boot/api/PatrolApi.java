package com.aiurt.boot.api;

import java.math.BigDecimal;
import java.util.Map;

//@FeignClient(value = "jeecg-patrol", fallbackFactory = PatrolFallback.class)
public interface PatrolApi {
    /**
     * 首页-获取统计日程的巡视完成数
     */
    Map<String, Integer> getPatrolFinishNumber(int year, int month);

    /**
     * 查看当前用户，当天的巡检的工单
     */
    public String getUserTask();

    /**
     * 大屏班组画像巡视工时统计
     */
    Map<String, BigDecimal> getPatrolUserHours(int type, String teamId);
}
