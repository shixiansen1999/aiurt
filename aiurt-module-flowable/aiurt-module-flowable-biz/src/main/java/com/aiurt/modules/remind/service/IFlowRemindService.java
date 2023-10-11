package com.aiurt.modules.remind.service;

import org.flowable.task.service.impl.persistence.entity.TaskEntity;

/**
 * @author fgw
 */
public interface IFlowRemindService {

    /**
     * 手工催办
     * @param processInstanceId
     */
    void manualRemind(String processInstanceId);

    /**
     * 超时提醒
     * @param taskEntity
     */
    void timeoutRemind(TaskEntity taskEntity);
}
