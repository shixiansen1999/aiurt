package com.aiurt.modules.deduplicate.service;


/**
 * @author fgw
 */
public interface IFlowDeduplicateService {

    /**
     * 审批人去重
     * @param processInstanceId
     * @param taskId
     */
    void handler(String processInstanceId, String taskId);
}
