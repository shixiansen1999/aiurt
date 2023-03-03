package com.aiurt.modules.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

                    List<String> processDefinitionIdList = StrUtil.split(executionEntity.getProcessDefinitionId(), ':');
                    if (CollectionUtil.isNotEmpty(processDefinitionIdList) && processDefinitionIdList.size()>0) {
                        // 流程标识
                        String modkelKey = processDefinitionIdList.get(0);
                        LambdaQueryWrapper<ActCustomModelInfo> wrapper = new LambdaQueryWrapper<>();
                        wrapper.eq(ActCustomModelInfo::getModelKey, modkelKey).last("limit 1");
                        IActCustomModelInfoService bean = SpringContextUtils.getBean(IActCustomModelInfoService.class);
                        ActCustomModelInfo one = bean.getOne(wrapper);
                        if (Objects.nonNull(one)) {
                            messageDTO.setProcessCode(one.getModelKey());
                            String name = StrUtil.contains(one.getName(), "流程") ? one.getName() : one.getName()+"流程";
                            messageDTO.setProcessName(name);
                        }
                    }
                    //map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.BPM.getType());
                    String startUserId = historicProcessInstance.getStartUserId();
                    Date startTime = historicProcessInstance.getStartTime();
                    ISysBaseAPI sysBaseAPI = SpringContextUtils.getBean(ISysBaseAPI.class);
                    LoginUser userByName = sysBaseAPI.getUserByName(startUserId);
                    String format = DateUtil.format(startTime, "yyyy-MM-dd");

                    map.put("creatBy",userByName.getRealname());
                    map.put("creatTime",format);
                    messageDTO.setData(map);

                    messageDTO.setTitle(historicProcessInstance.getProcessDefinitionName()+"-"+userByName.getRealname()+"-"+DateUtil.format(startTime, "yyyy-MM-dd HH:mm:ss"));
                    messageDTO.setFromUser( loginUser.getUsername());
                    messageDTO.setToUser(historicProcessInstance.getStartUserId());
                    messageDTO.setToAll(false);
                    messageDTO.setTemplateCode(CommonConstant.BPM_SERVICE_NOTICE);
                    ISysParamAPI bean = SpringContextUtils.getBean(ISysParamAPI.class);
                    SysParamModel sysParamModel = bean.selectByCode(SysParamCodeConstant.BPM_MESSAGE);
                    messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                    messageDTO.setMsgAbstract("你有一条流程消息");
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
