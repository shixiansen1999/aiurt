package com.aiurt.modules.listener;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
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
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecg.common.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @Author cjb
 * @Date 2023-01-05
 * @Description: 流程结束后发送消息给提交人
 */
public class ProcessCompletedListener implements Serializable, FlowableEventListener {

    private transient final Logger logger = LoggerFactory.getLogger(ProcessCompletedListener.class);
    @Autowired
    private ISysParamAPI iSysParamAPI;
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
                    /*iSysBaseApi.sendBusAnnouncement(
                            new BusMessageDTO(
                                    loginUser.getUsername(),
                                    historicProcessInstance.getStartUserId(),
                                    historicProcessInstance.getName(),
                                    msgContent,
                                    CommonConstant.MSG_CATEGORY_2,
                                    SysAnnmentTypeEnum.BPM.getType(),
                                    historicProcessInstance.getBusinessKey()
                            )
                    );*/
                    // 发消息
                    MessageDTO messageDTO = new MessageDTO();
                    //构建消息模板
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, historicProcessInstance.getBusinessKey());
                    map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.BPM.getType());
                    map.put("msgContent", msgContent);
                    messageDTO.setData(map);

                    messageDTO.setTitle(historicProcessInstance.getName());
                    messageDTO.setFromUser( loginUser.getUsername());
                    messageDTO.setToUser(historicProcessInstance.getStartUserId());
                    messageDTO.setToAll(false);
                    messageDTO.setTemplateCode(CommonConstant.BPM_SERVICE_NOTICE);
                    SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.BPM_MESSAGE);
                    messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                    messageDTO.setMsgAbstract("你有一条流程消息");
                    messageDTO.setPublishingContent("你有一条流程消息");
                    messageDTO.setCategory(CommonConstant.MSG_CATEGORY_2);
                    iSysBaseApi.sendTemplateMessage(messageDTO);

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
