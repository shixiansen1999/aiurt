package com.aiurt.modules.listener;

import cn.hutool.core.date.DateUtil;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.flowable.engine.delegate.event.impl.FlowableProcessStartedEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
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
        logger.info("流程启动监听事件");
        if (event instanceof FlowableProcessStartedEventImpl) {
            FlowableProcessStartedEventImpl processStartedEvent = (FlowableProcessStartedEventImpl) event;

            Object entity = processStartedEvent.getEntity();

            if (entity instanceof ExecutionEntity) {
                ExecutionEntity executionEntity = (ExecutionEntity) entity;

                String processDefinitionId = executionEntity.getProcessDefinitionId();
                ProcessDefinition processDefinition = ProcessEngines.getDefaultProcessEngine().getRepositoryService().getProcessDefinition(processDefinitionId);

                String deploymentId = processDefinition.getDeploymentId();

                Deployment deployment = ProcessEngines.getDefaultProcessEngine().getRepositoryService().createDeploymentQuery().deploymentId(deploymentId).singleResult();

                //获取流程名称
                String name = deployment.getName();

                String format = DateUtil.format(new Date(), "(yyyy-MM-dd HH:mm:ss)");

                String processName = String.format("%s%s", name, format);

                ProcessEngines.getDefaultProcessEngine().getRuntimeService().setProcessInstanceName(executionEntity.getProcessInstanceId(), processName);

            }
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
