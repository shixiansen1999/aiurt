package com.aiurt.modules.api;

import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Service;

import java.util.Date;
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
}
