package com.aiurt.modules.emptyuser;

import org.flowable.task.service.impl.persistence.entity.TaskEntity;

public interface IEmptyUserService {

    /**
     * 审批人为空处理
     * @param taskEntity
     */
    public void handEmptyUserName(TaskEntity taskEntity);
}
