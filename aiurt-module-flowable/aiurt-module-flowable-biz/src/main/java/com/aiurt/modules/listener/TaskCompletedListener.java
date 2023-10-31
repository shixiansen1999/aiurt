package com.aiurt.modules.listener;

import com.aiurt.modules.common.constant.FlowModelExtElementConstant;
import com.aiurt.modules.utils.FlowableNodeActionUtils;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityWithVariablesEventImpl;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.job.service.TimerJobService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fgw
 * @date 2022-12-22
 */
public class TaskCompletedListener implements FlowableEventListener {

    private static Logger logger = LoggerFactory.getLogger(TaskCompletedListener.class);

    /**
     * Called when an event has been fired
     *
     * @param event the event
     */
    @Override
    public void onEvent(FlowableEvent event) {
        if (!(event instanceof FlowableEntityWithVariablesEventImpl)) {
            return;
        }
        FlowableEntityWithVariablesEventImpl flowableEntityEvent = (FlowableEntityWithVariablesEventImpl) event;
        Object entity = flowableEntityEvent.getEntity();
        if (!(entity instanceof TaskEntity)) {
            logger.debug("活动启动监听事件,实体类型不对");
            return;
        }


        TaskEntity taskEntity = (TaskEntity) entity;
        String id = taskEntity.getId();
        // 流程定义id
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        // 流程实例id
        String processInstanceId = taskEntity.getProcessInstanceId();
        // 流程节点定义id
        String taskDefinitionKey = taskEntity.getTaskDefinitionKey();
        if (logger.isDebugEnabled()) {
            logger.debug("任务提交事件, 任务id：{}， 节点id：{}，流程实例id：{}", id, taskDefinitionKey, processInstanceId);
        }
        try {
            // 任务节点前附加操作
            FlowableNodeActionUtils.processTaskData(taskEntity, processDefinitionId, taskDefinitionKey, processInstanceId, FlowModelExtElementConstant.EXT_POST_NODE_ACTION);
            if (logger.isInfoEnabled()) {
                logger.info("任务提交事件, 节点后操作事件处理 任务id：{}， 节点id：{}，流程实例id：{}", id, taskDefinitionKey, processInstanceId);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        //删除定时任务
        String executionId = taskEntity.getExecutionId();
        TimerJobService timerJobService = CommandContextUtil.getTimerJobService();
        timerJobService.deleteTimerJobsByExecutionId(executionId);
        if (logger.isInfoEnabled()) {
            logger.info("任务提交事件, 删除超时定时任务, 任务id：{}， 节点id：{}，流程实例id：{}", id, taskDefinitionKey, processInstanceId);
            logger.info("任务提交事件, 业务处理结束, 任务id：{}， 节点id：{}，流程实例id：{}", id, taskDefinitionKey, processInstanceId);
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
