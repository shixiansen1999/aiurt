package com.aiurt.boot.api;

import com.aiurt.boot.manager.dto.FaultCallbackDTO;


public interface inspectionApi {

    /**
     * 故障回调
     * @param faultCallbackDTO
     */
    void editFaultCallback(FaultCallbackDTO faultCallbackDTO);
}
