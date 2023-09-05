package com.aiurt.modules.remind.service;

/**
 * @author fgw
 */
public interface IFlowRemindService {

    /**
     * 手工催办
     * @param processInstanceId
     */
    void manualRemind(String processInstanceId);
}
