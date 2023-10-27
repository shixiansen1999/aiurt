package com.aiurt.modules.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.util.SpringContextUtils;


/**
 *
 * TaskCompleted-> entityDelete
 * @author gaowei
 */
@Slf4j
public class EntityDeletedListener implements FlowableEventListener {

    @Override
    public void onEvent(FlowableEvent flowableEvent) {
        if (!(flowableEvent instanceof FlowableEntityEventImpl)) {
            if (log.isDebugEnabled()) {
                log.debug("事件类型不符合要求，结束");
            }
            return;
        }

        FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) flowableEvent;
        Object entity = flowableEntityEvent.getEntity();
        if (!(entity instanceof TaskEntityImpl)) {
            if (log.isDebugEnabled()) {
                log.debug("活动删除事件,实体类型不对，结束业务处理");
            }
            return;
        }

        TaskEntityImpl taskEntity = (TaskEntityImpl) entity;
        String id = taskEntity.getId();

        ISTodoBaseAPI todoBaseApi = SpringContextUtils.getBean(ISTodoBaseAPI.class);
        todoBaseApi.updateBpmnTaskState(id, taskEntity.getProcessInstanceId(), taskEntity.getAssignee(), "1");
        if (log.isDebugEnabled()) {
            log.debug("活动删除事件,更新代办状态 任务id：{}， 节点id：{}，流程实例id：{}", id, taskEntity.getTaskDefinitionKey(),
                    taskEntity.getProcessInstanceId());
        }
      
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
