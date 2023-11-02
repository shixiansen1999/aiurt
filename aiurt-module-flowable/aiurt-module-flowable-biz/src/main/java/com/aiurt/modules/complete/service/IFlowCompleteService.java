package com.aiurt.modules.complete.service;


import com.aiurt.modules.complete.dto.FlowCompleteReqDTO;

/**
 * @author: fgw
 * @date: 2023/08/10 09：48
 * @description:
 */
public interface IFlowCompleteService {

    /**
     * 流程提交
     * @param flowCompleteReqDTO
     */
    void complete(FlowCompleteReqDTO flowCompleteReqDTO);
}
