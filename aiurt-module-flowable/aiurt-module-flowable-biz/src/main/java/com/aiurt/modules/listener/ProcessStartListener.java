package com.aiurt.modules.listener;

import cn.hutool.core.date.DateUtil;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.delegate.event.impl.FlowableProcessStartedEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fgw
 * @date 2022-10-19
 */
public class ProcessStartListener implements Serializable, FlowableEventListener {

    private transient final Logger logger = LoggerFactory.getLogger(ProcessStartListener.class);

    @Override
    public void onEvent(FlowableEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("流程启动监听事件业务处理开始");
        }
        if (event instanceof FlowableProcessStartedEventImpl) {
            FlowableProcessStartedEventImpl processStartedEvent = (FlowableProcessStartedEventImpl) event;

            Object entity = processStartedEvent.getEntity();

            if (entity instanceof ExecutionEntity) {
                ExecutionEntity executionEntity = (ExecutionEntity) entity;
                if (logger.isDebugEnabled()) {
                    logger.debug("流程启动监听事件,流程实例id：{}， 发起用户：{}", executionEntity.getProcessInstanceId(), executionEntity.getStartUserId());
                }
                // 获取流程名称
                String name = executionEntity.getProcessDefinitionName();

                String format = DateUtil.format(new Date(), "yyyy-MM-dd");

                String processName = String.format("%s-%s", name, format);
                if (logger.isDebugEnabled()) {
                    logger.debug("流程启动监听事件设置流程名称：{}", processName);
                }
                ProcessEngines.getDefaultProcessEngine().getRuntimeService().setProcessInstanceName(executionEntity.getProcessInstanceId(), processName);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("流程启动监听事件业务处理结束");
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
