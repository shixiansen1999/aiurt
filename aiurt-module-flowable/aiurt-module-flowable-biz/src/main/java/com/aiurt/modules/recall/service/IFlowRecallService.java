package com.aiurt.modules.recall.service;

import com.aiurt.modules.recall.dto.RecallReqDTO;

/**
 * @author fgw
 */
public interface IFlowRecallService {


    /**
     * 撤回
     * @param recallReqDTO
     */
    void recall(RecallReqDTO recallReqDTO);
}
