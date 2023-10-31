package com.aiurt.modules.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.flow.enums.FlowStatesEnum;
import com.aiurt.modules.flow.service.IActCustomFlowStateService;
import org.apache.shiro.SecurityUtils;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.job.service.TimerJobService;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntity;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecg.common.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
                if (logger.isInfoEnabled()) {
                    logger.info("流程结束监听事件, 删除该流程流程实例的定时任务，历史实例id：{}", executionEntity.getProcessInstanceId());
                }

                IActCustomFlowStateService flowStateService = SpringContextUtils.getBean(IActCustomFlowStateService.class);
                flowStateService.updateFlowState(executionEntity.getProcessInstanceId(), FlowStatesEnum.COMPLETE.getCode());
                if (logger.isInfoEnabled()) {
                    logger.info("流程结束监听事件, 更新流程状态，历史实例id：{}，流程状态:{}", executionEntity.getProcessInstanceId(),
                            FlowStatesEnum.COMPLETE.getCode());
                }

                try {
                    sendMessage(executionEntity);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("流程结束监听事件, 处理业务数据结束");
        }
    }

    private void sendMessage(ExecutionEntity executionEntity) {
        HistoryService historyService = ProcessEngines.getDefaultProcessEngine().getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(executionEntity.getProcessInstanceId()).singleResult();


        Boolean variableLocal = executionEntity.getVariableLocal(FlowConstant.STOP_PROCESS, Boolean.class);
        if (Objects.nonNull(variableLocal)) {
            return;
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ISysBaseAPI iSysBaseApi = SpringContextUtils.getBean(ISysBaseAPI.class);
        // 发消息
        MessageDTO messageDTO = new MessageDTO();
        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, historicProcessInstance.getBusinessKey());
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.BPM.getType());

        String definitionName = historicProcessInstance.getProcessDefinitionName();
        messageDTO.setProcessName(definitionName);
        definitionName = StrUtil.contains(definitionName, "流程") ? definitionName : "【"+definitionName + "】流程";
        messageDTO.setProcessDefinitionKey(historicProcessInstance.getProcessDefinitionKey());
        String startUserId = historicProcessInstance.getStartUserId();
        Date startTime = historicProcessInstance.getStartTime();
        ISysBaseAPI sysBaseAPI = SpringContextUtils.getBean(ISysBaseAPI.class);
        LoginUser userByName = sysBaseAPI.getUserByName(startUserId);
        String format = DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN);

        map.put("creatBy", userByName.getRealname());
        map.put("creatTime", format);
        map.put("endTime",  DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        messageDTO.setData(map);
        messageDTO.setTaskId(executionEntity.getId());
        messageDTO.setProcessInstanceId(executionEntity.getProcessInstanceId());
        messageDTO.setTitle(definitionName);
        messageDTO.setFromUser(loginUser.getUsername());
        messageDTO.setToUser(historicProcessInstance.getStartUserId());
        messageDTO.setToAll(false);
        messageDTO.setProcessCode(historicProcessInstance.getProcessDefinitionKey());
        messageDTO.setTemplateCode("bpm_service_complete_process");
        ISysParamAPI bean = SpringContextUtils.getBean(ISysParamAPI.class);
        SysParamModel sysParamModel = bean.selectByCode(SysParamCodeConstant.BPM_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        messageDTO.setMsgAbstract("有流程【归档】提醒");
        iSysBaseApi.sendTemplateMessage(messageDTO);

        if (logger.isInfoEnabled()) {
            logger.info("流程结束监听事件, 发送消息，历史实例id：{}，流程状态", executionEntity.getProcessInstanceId(),
                    FlowStatesEnum.COMPLETE.getCode());
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
