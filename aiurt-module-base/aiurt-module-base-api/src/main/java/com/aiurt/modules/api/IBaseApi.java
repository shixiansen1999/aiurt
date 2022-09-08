package com.aiurt.modules.api;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;

import java.util.List;
import java.util.Map;

public interface IBaseApi {

    /**
     * 查询
     * @return
     */
    public Map<String, List<DailySchedule>> queryDailyScheduleList(Integer year, Integer month);
}
