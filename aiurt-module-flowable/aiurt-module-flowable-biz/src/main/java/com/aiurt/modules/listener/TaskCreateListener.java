package com.aiurt.modules.listener;

import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fgw
 * @date 2022-10-12
 */
public class TaskCreateListener implements FlowableEventListener {

    private static Logger logger = LoggerFactory.getLogger(TaskCreateListener.class);

    /**
     * Called when an event has been fired
     *
     * @param event the event
     */
    @Override
    public void onEvent(FlowableEvent event) {
        logger.info("start task create listener");
        if (!(event instanceof FlowableEntityEventImpl)) {
            return;
        }
        FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) event;
        Object entity = flowableEntityEvent.getEntity();
        if (!(entity instanceof TaskEntity)) {
            logger.debug("活动启动监听事件,实体类型不对");
            return;
        }

        logger.debug("活动启动监听事件,设置办理人员......");
        TaskEntity taskEntity = (TaskEntity) entity;
        String taskId = taskEntity.getId();

        // 查询相关配置
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
