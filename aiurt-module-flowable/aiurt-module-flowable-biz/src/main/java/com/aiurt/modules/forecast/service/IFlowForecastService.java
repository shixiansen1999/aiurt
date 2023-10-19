package com.aiurt.modules.forecast.service;

import com.aiurt.modules.flow.dto.HighLightedNodeDTO;
import com.aiurt.modules.forecast.dto.HistoryTaskInfo;

import java.util.LinkedHashMap;

/**
 * @author fgw
 */
public interface IFlowForecastService {

    /**
     * 流程预测
     * @param processInstanceId 流程实例id
     * @return
     */
    public HighLightedNodeDTO flowChart(String processInstanceId);


    /**
     * 合并任务
     * @param processInstanceId 流程实例id
     * @return
     */
    LinkedHashMap<String, HistoryTaskInfo> mergeTask(String processInstanceId);
}
