package com.aiurt.modules.listener;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.deduplicate.service.IFlowDeduplicateService;
import com.aiurt.modules.emptyuser.IEmptyUserService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.jeecg.common.util.SpringContextUtils;

/**
 * 任务设置办理人监听器， <p>指派事件是在创建事件之前执行，在触发create事件之前触发assignmen，因为时机上先触发了assignment，
 * 但是此时还未指定人（指定人是在后续的create事件中），所以不会触发assignmen事件</p>
 * @author fgw ASSIGNED
 */
@Slf4j
public class TaskAssignedListener implements FlowableEventListener {

    private static final String AUTO = "_AUTO_";
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
        if (log.isDebugEnabled()) {
            log.debug("任务设置办理人事件，实例id：{}， 任务id：{}， 节点id：{}， 节点名称：{}, 办理人：{}", taskEntity.getProcessInstanceId(),
                    taskEntity.getId(), taskEntity.getTaskDefinitionKey(), taskEntity.getName(), taskEntity.getAssignee());
        }
        if (StrUtil.startWith(taskEntity.getAssignee(), AUTO)) {
            if (log.isDebugEnabled()) {
                log.debug("任务办理人为空处理开始");
            }
            IEmptyUserService emptyUserService = SpringContextUtils.getBean(IEmptyUserService.class);
            emptyUserService.handEmptyUserName(taskEntity);
            if (log.isDebugEnabled()) {
                log.debug("任务办理人为空处理结束");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("任务审批人处理开始");
            }
            IFlowDeduplicateService deduplicateService = SpringContextUtils.getBean(IFlowDeduplicateService.class);
            deduplicateService.handler(taskEntity);
            if (log.isDebugEnabled()) {
                log.debug("任务审批人处理结束");
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("任务设置办理人事件, 业务处理结束 实例id：{}， 任务id：{}， 节点id：{}， 节点名称：{}, 办理人：{}", taskEntity.getProcessInstanceId(),
                    taskEntity.getId(), taskEntity.getTaskDefinitionKey(), taskEntity.getName(), taskEntity.getAssignee());
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
