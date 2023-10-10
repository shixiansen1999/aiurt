package com.aiurt.modules.forecast.service;

import com.aiurt.modules.flow.dto.HighLightedNodeDTO;

/**
 * @author fgw
 */
public interface IFlowForecastService {

    /**
     * 流程预测
     * @param processInstanceId
     * @return
     */
    public HighLightedNodeDTO flowChart(String processInstanceId);
}
