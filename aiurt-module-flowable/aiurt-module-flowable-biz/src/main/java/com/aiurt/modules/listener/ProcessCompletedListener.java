package com.aiurt.modules.listener;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.api.dto.message.BusMessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import org.apache.shiro.SecurityUtils;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @Author cjb
 * @Date 2023-01-05
 * @Description: 流程结束后发送消息给提交人
 */
public class ProcessCompletedListener implements Serializable, FlowableEventListener {

    private transient final Logger logger = LoggerFactory.getLogger(ProcessCompletedListener.class);

    @Override
    public void onEvent(FlowableEvent event) {
        logger.info("流程结束监听事件");
        if (event instanceof FlowableEntityEventImpl) {
            FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) event;
            FlowableEngineEventType type = flowableEntityEvent.getType();
            if (FlowableEngineEventType.PROCESS_COMPLETED.equals(type)) {
                Object entity = flowableEntityEvent.getEntity();
                ExecutionEntity executionEntity = (ExecutionEntity) entity;

                String msgContent = "您有一条任务的流程已审批完成！";
                RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();
                Boolean variable = (Boolean) runtimeService.getVariable(executionEntity.getProcessInstanceId(), FlowModelAttConstant.CANCEL);
                if (ObjectUtil.isNotEmpty(variable) && variable) {
                    msgContent = "您有一条任务的流程已被取消！";
                }

                HistoricProcessInstance historicProcessInstance = ProcessEngines.getDefaultProcessEngine().getHistoryService()
                        .createHistoricProcessInstanceQuery()
                        .processInstanceId(executionEntity.getProcessInstanceId())
                        .singleResult();
                try {
                    LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    ISysBaseAPI iSysBaseApi = SpringContextUtils.getBean(ISysBaseAPI.class);
                    iSysBaseApi.sendBusAnnouncement(
                            new BusMessageDTO(
                                    loginUser.getUsername(),
                                    historicProcessInstance.getStartUserId(),
                                    historicProcessInstance.getName(),
                                    msgContent,
                                    CommonConstant.MSG_CATEGORY_2,
                                    SysAnnmentTypeEnum.BPM.getType(),
                                    historicProcessInstance.getBusinessKey()
                            )
                    );
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
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
