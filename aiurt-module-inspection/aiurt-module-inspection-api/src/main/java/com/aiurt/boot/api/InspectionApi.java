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
}
