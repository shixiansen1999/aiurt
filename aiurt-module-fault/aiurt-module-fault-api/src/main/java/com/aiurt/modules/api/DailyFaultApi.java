package com.aiurt.modules.api;

import org.springframework.stereotype.Service;

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
}
