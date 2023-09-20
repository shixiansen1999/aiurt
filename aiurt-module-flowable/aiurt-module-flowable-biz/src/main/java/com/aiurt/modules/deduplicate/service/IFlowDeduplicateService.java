package com.aiurt.modules.deduplicate.service;


import org.flowable.task.api.Task;

/**
 * @author fgw
 */
public interface IFlowDeduplicateService {

    /**
     * 审批人去重
     * @param processInstanceId
     * @param taskId
     */
    void handler(Task task);
}
