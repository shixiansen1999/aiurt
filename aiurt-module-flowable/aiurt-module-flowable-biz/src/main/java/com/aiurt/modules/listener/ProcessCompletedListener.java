package com.aiurt.modules.listener;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.flow.enums.FlowStatesEnum;
import com.aiurt.modules.flow.service.IActCustomFlowStateService;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.job.service.TimerJobService;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntity;
import org.jeecg.common.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * @Author cjb
 * @Date 2023-01-05
 * @Description: 流程结束后发送消息给提交人
 */
public class ProcessCompletedListener implements Serializable, FlowableEventListener {

    private transient final Logger logger = LoggerFactory.getLogger(ProcessCompletedListener.class);
    @Override
    public void onEvent(FlowableEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("流程结束监听事件, 处理业务数据开始");
        }
        if (event instanceof FlowableEntityEventImpl) {
            FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) event;
            FlowableEngineEventType type = flowableEntityEvent.getType();
            if (FlowableEngineEventType.PROCESS_COMPLETED.equals(type)) {
                Object entity = flowableEntityEvent.getEntity();
                ExecutionEntity executionEntity = (ExecutionEntity) entity;

                TimerJobService timerJobService = CommandContextUtil.getTimerJobService();
                List<TimerJobEntity> timerJobEntityList = timerJobService
                        .findTimerJobsByProcessInstanceId(executionEntity.getProcessInstanceId());
                if (CollUtil.isNotEmpty(timerJobEntityList)) {
                    timerJobEntityList.stream().forEach(timerJobEntity -> timerJobService.deleteTimerJob(timerJobEntity));
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("流程结束监听事件, 删除该流程流程实例的定时任务，历史实例id：{}", executionEntity.getProcessInstanceId());
                }

                IActCustomFlowStateService flowStateService = SpringContextUtils.getBean(IActCustomFlowStateService.class);
                flowStateService.updateFlowState(executionEntity.getProcessInstanceId(), FlowStatesEnum.COMPLETE.getCode());
                if (logger.isDebugEnabled()) {
                    logger.debug("流程结束监听事件, 更新流程状态，历史实例id：{}，流程状态", executionEntity.getProcessInstanceId(),
                            FlowStatesEnum.COMPLETE.getCode());
                }

                // todo， 发送消息
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("流程结束监听事件, 处理业务数据结束");
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
