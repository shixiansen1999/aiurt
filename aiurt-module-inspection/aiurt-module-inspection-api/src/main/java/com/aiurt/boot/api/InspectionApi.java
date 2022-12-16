package com.aiurt.boot.api;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.manager.dto.FaultCallbackDTO;

import java.util.HashMap;

/**
 * @author zwl
 */
public interface InspectionApi {

    /**
     * 故障回调
     * @param faultCallbackDTO
     */
    void editFaultCallback(FaultCallbackDTO faultCallbackDTO);

    /**
     *获取当前用户在时间范围内提交的工单
     * @param startTime
     * @param endTime
     * @return
     */
    HashMap<String, String> getInspectionTaskDevice(DateTime startTime, DateTime endTime);
}
