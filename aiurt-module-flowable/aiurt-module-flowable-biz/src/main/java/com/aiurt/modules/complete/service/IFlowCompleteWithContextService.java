package com.aiurt.modules.complete.service;

import com.aiurt.modules.complete.dto.CompleteTaskContext;
import com.aiurt.modules.complete.dto.FlowCompleteReqDTO;

/**
 * @author fgw
 */
public interface IFlowCompleteWithContextService extends IFlowCompleteService {

    /**
     * 流程提交
     * @param flowCompleteReqDTO
     * @param completeTaskContext
     */
    void complete(FlowCompleteReqDTO flowCompleteReqDTO, CompleteTaskContext completeTaskContext);
}
