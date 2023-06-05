package com.aiurt.boot.api;

import java.util.Map;

/**
 * @author
 * @description: 报表统计-人员画像
 */
public interface PersonnelPortraitPatrolApi {
    /**
     * 获取近五年的巡视任务数据
     *
     * @param userId
     * @return
     */
    Map<Integer, Long> getPatrolTaskNumber(String userId, int flagYearAgo, int thisYear);
}
