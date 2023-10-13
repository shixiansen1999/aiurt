package com.aiurt.modules.listener;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.deduplicate.service.IFlowDeduplicateService;
import com.aiurt.modules.emptyuser.IEmptyUserService;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.jeecg.common.util.SpringContextUtils;

/**
 * 任务设置办理人监听器
 * @author fgw ASSIGNED
 */
public class TaskAssignedListener implements FlowableEventListener {
    /**
     * Called when an event has been fired
     *
     * @param event the event
     */
    @Override
    public void onEvent(FlowableEvent event) {
        FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) event;
        Object entity = flowableEntityEvent.getEntity();
        if (!(entity instanceof TaskEntity)) {
            return;
        }
        TaskEntity taskEntity = (TaskEntity) entity;
        if (StrUtil.startWith(taskEntity.getAssignee(), "_AUTO_")) {
            IEmptyUserService emptyUserService = SpringContextUtils.getBean(IEmptyUserService.class);
            emptyUserService.handEmptyUserName(taskEntity);
        } else {
            IFlowDeduplicateService deduplicateService = SpringContextUtils.getBean(IFlowDeduplicateService.class);
            deduplicateService.handler(taskEntity);
        }
    }

    /**
     * @return whether or not the current operation should fail when this listeners execution throws an exception.
     */
    @Override
    public boolean isFailOnException() {
        return false;
    }

    /**
     * @return Returns whether this event listener fires immediately when the event occurs or
     * on a transaction lifecycle event (before/after commit or rollback).
     */
    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    /**
     * @return if non-null, indicates the point in the lifecycle of the current transaction when the event should be fired.
     */
    @Override
    public String getOnTransaction() {
        return null;
    }
}
