package com.aiurt.boot.api;

import java.util.Map;

//@FeignClient(value = "jeecg-patrol", fallbackFactory = PatrolFallback.class)
public interface PatrolApi {
    /**
     * 首页-获取统计日程的巡视完成数
     */
    Map<String, Integer> getPatrolFinishNumber(int year, int month);
}
