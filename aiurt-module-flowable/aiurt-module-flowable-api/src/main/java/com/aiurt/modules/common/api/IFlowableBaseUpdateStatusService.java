package com.aiurt.modules.common.api;

import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;

/**
 * @author fgw
 *
 */
public interface IFlowableBaseUpdateStatusService {

    /**
     * 驳回第一个节点
     * @param entity
     */
    void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity);


    /**
     * 更新状态
     * @param updateStateEntity
     */
    void  updateState(UpdateStateEntity updateStateEntity);
}
