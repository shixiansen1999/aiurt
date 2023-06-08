package com.aiurt.boot.api;

import java.util.Map;

/**
 * @author
 * @description: 报表统计-人员画像
 */
public interface PersonnelPortraitInspectionApi {

    /**
     * 获取近五年的检修任务数据
     *
     * @param userId
     * @return
     */
    Map<Integer, Long> getInspectionNumber(String userId, int flagYearAgo, int thisYear);
}
