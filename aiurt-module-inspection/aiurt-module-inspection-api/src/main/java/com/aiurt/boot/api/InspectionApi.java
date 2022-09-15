package com.aiurt.boot.api;

import com.aiurt.boot.manager.dto.FaultCallbackDTO;

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
     *获取当前用户，当天提交的工单
     * @return
     */
    String getInspectionTaskDevice();
}
