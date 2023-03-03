package com.aiurt.modules.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.todo.dto.BpmnTodoDTO;
import com.aiurt.modules.user.service.IFlowUserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecg.common.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author fgw
 * @date 2022-10-12
 */
public class TaskCreateListener implements FlowableEventListener {

    private static Logger logger = LoggerFactory.getLogger(TaskCreateListener.class);
    /**
     * Called when an event has been fired
     *
     * @param event the event
     */
    @Override
    public void onEvent(FlowableEvent event) {
        logger.info("start task create listener");
        if (!(event instanceof FlowableEntityEventImpl)) {
            return;
        }
        FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) event;
        Object entity = flowableEntityEvent.getEntity();
        if (!(entity instanceof TaskEntity)) {
            logger.debug("活动启动监听事件,实体类型不对");
            return;
        }

        logger.debug("活动启动监听事件,设置办理人员......");
        TaskEntity taskEntity = (TaskEntity) entity;
        // 流程任务id
        String taskId = taskEntity.getId();
        // 流程定义id
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        // 流程实例id
        String processInstanceId = taskEntity.getProcessInstanceId();
        // 流程节点定义id
        String taskDefinitionKey = taskEntity.getTaskDefinitionKey();
        // 查询流程实例
        ProcessInstance instance = ProcessEngines.getDefaultProcessEngine().getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        FlowElementUtil flowElementUtil = SpringContextUtils.getBean(FlowElementUtil.class);

        // 查询配置项
        IActCustomTaskExtService taskExtService = SpringContextUtils.getBean(IActCustomTaskExtService.class);
        ActCustomTaskExt taskExt = taskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, taskDefinitionKey);

        // 判断首个节点是否为驳回
        UserTask userTask = flowElementUtil.getFirstUserTaskByDefinitionId(processDefinitionId);
        if (Objects.nonNull(userTask) && StrUtil.equalsAnyIgnoreCase(userTask.getId(), taskDefinitionKey)) {
            HistoryService historyService = ProcessEngines.getDefaultProcessEngine().getHistoryService();
            List<HistoricTaskInstance> instanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                    .taskDefinitionKey(taskDefinitionKey).finished().orderByTaskCreateTime().desc().list();
            if (CollectionUtil.isNotEmpty(instanceList) && instanceList.size()>1) {
                HistoricTaskInstance historicTaskInstance = instanceList.get(0);
                String assignee = historicTaskInstance.getAssignee();
                if (StrUtil.isBlank(assignee)) {
                    String initiator = ProcessEngines.getDefaultProcessEngine().getRuntimeService()
                            .getVariable(processInstanceId, FlowConstant.PROC_INSTANCE_INITIATOR_VAR, String.class);
                    ProcessEngines.getDefaultProcessEngine().getTaskService().setAssignee(taskId, initiator);
                    buildToDoList(taskEntity, instance, taskExt, Collections.singletonList(initiator));
                }else {
                    ProcessEngines.getDefaultProcessEngine().getTaskService().setAssignee(taskId, assignee);
                    buildToDoList(taskEntity, instance, taskExt, Collections.singletonList(assignee));
                }
                return;
            }else {
                // 第一个任务设置为发起人
                String initiator = ProcessEngines.getDefaultProcessEngine().getRuntimeService()
                        .getVariable(processInstanceId, FlowConstant.PROC_INSTANCE_INITIATOR_VAR, String.class);
                ProcessEngines.getDefaultProcessEngine().getTaskService().setAssignee(taskId, initiator);
                buildToDoList(taskEntity, instance, taskExt, Collections.singletonList(initiator));
                return;
            }
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 如果没有配置选人信息
        if (Objects.isNull(taskExt) && Objects.nonNull(loginUser)) {
            // 设置当前登录人员
            ProcessEngines.getDefaultProcessEngine().getTaskService().setAssignee(taskId, loginUser.getUsername());
        }
        // 没有配置则选择发起人
        String groupType = taskExt.getGroupType();
        if (StrUtil.isBlank(groupType)) {
            String initiator = ProcessEngines.getDefaultProcessEngine().getRuntimeService()
                    .getVariable(processInstanceId, FlowConstant.PROC_INSTANCE_INITIATOR_VAR, String.class);
            ProcessEngines.getDefaultProcessEngine().getTaskService().setAssignee(taskId, initiator);
            buildToDoList(taskEntity, instance, taskExt, Collections.singletonList(initiator));
            return;
        }
        IFlowUserService flowUserService = SpringContextUtils.getBean(IFlowUserService.class);
        List<String> userNameList = new ArrayList<>();
        switch (groupType) {
            // 角色
            case "candidateRole":
                String roleIds = taskExt.getRoleIds();
                userNameList = flowUserService.getUserNameByRoleId(StrUtil.splitTrim(roleIds, ','));
                break;
            // 候选人员,指定人员
            case "candidateUsers":
            case "assignee":
                String candidateUsernames = taskExt.getCandidateUsernames();
                userNameList = flowUserService.getUserNameByUserId(StrUtil.splitTrim(candidateUsernames, ','));
                break;
            // 机构
            case "candidateDept":
                String deptIds = taskExt.getDeptIds();
                userNameList = flowUserService.getUserNameByOrgId(StrUtil.splitTrim(deptIds, ','));
                break;
            // 动态
            case "dynamic":
                String dynamicVariable = taskExt.getDynamicVariable();
                String variable = ProcessEngines.getDefaultProcessEngine().getRuntimeService()
                        .getVariable(processInstanceId, dynamicVariable, String.class);
                if (StrUtil.isNotBlank(variable)) {
                    userNameList = flowUserService.getUserName(variable);
                }
                break;
            // 流程发起人
            default:
                String initiator = ProcessEngines.getDefaultProcessEngine().getRuntimeService()
                        .getVariable(processInstanceId, FlowConstant.PROC_INSTANCE_INITIATOR_VAR, String.class);
                userNameList.add(initiator);
                break;
        }

        if (CollectionUtil.isEmpty(userNameList)) {
            String initiator = ProcessEngines.getDefaultProcessEngine().getRuntimeService()
                    .getVariable(processInstanceId, FlowConstant.PROC_INSTANCE_INITIATOR_VAR, String.class);
            userNameList.add(initiator);
        }

        if (userNameList.size() ==1) {
            logger.info("设置办理人:{},{}", taskId, userNameList.get(0));
            ProcessEngines.getDefaultProcessEngine().getTaskService().setAssignee(taskId, userNameList.get(0));
        }else {
            for (String userName : userNameList) {
                ProcessEngines.getDefaultProcessEngine().getTaskService().addCandidateUser(taskId, userName);
            }
        }

        // 创建任务
        buildToDoList(taskEntity, instance, taskExt, userNameList);

    }

    private void buildToDoList(TaskEntity taskEntity, ProcessInstance instance, ActCustomTaskExt taskExt, List<String> userNameList) {
        try {

            BpmnTodoDTO bpmnTodoDTO = new BpmnTodoDTO();
            bpmnTodoDTO.setTaskKey(taskEntity.getTaskDefinitionKey());
            bpmnTodoDTO.setTaskId(taskEntity.getId());
            bpmnTodoDTO.setProcessInstanceId(taskEntity.getProcessInstanceId());
            String processDefinitionId = taskEntity.getProcessDefinitionId();
            bpmnTodoDTO.setProcessDefinitionKey(processDefinitionId);
            bpmnTodoDTO.setTaskName(taskEntity.getName());
            bpmnTodoDTO.setBusinessKey(instance.getBusinessKey());
            bpmnTodoDTO.setCurrentUserName(StrUtil.join(",", userNameList));
            bpmnTodoDTO.setTodoType("0");
            bpmnTodoDTO.setProcessDefinitionName(instance.getName());
            String formJson = taskExt.getFormJson();
            if (StrUtil.isNotBlank(formJson)) {
                JSONObject json = JSONObject.parseObject(formJson);
                if (Objects.nonNull(json)) {
                    bpmnTodoDTO.setUrl(json.getString("formUrl"));
                    bpmnTodoDTO.setUrlType(json.getString("formType"));
                }
            }
            HashMap<String, Object> map = new HashMap<>();
            // 处理流程
            List<String> processDefinitionIdList = StrUtil.split(processDefinitionId, ':');
            if (CollectionUtil.isNotEmpty(processDefinitionIdList) && processDefinitionIdList.size()>0) {
                // 流程标识
                String modkelKey = processDefinitionIdList.get(0);
                LambdaQueryWrapper<ActCustomModelInfo> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ActCustomModelInfo::getModelKey, modkelKey).last("limit 1");
                IActCustomModelInfoService bean = SpringContextUtils.getBean(IActCustomModelInfoService.class);
                ActCustomModelInfo one = bean.getOne(wrapper);
                if (Objects.nonNull(one)) {
                    bpmnTodoDTO.setProcessCode(one.getModelKey());
                    String name = StrUtil.contains(one.getName(), "流程") ? one.getName() : one.getName()+"流程";
                    bpmnTodoDTO.setProcessName(name);
                }
                map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,processDefinitionIdList.get(0));
            }

            //发送待办
            String startUserId = instance.getStartUserId();
            Date startTime = instance.getStartTime();
            ISysBaseAPI bean = SpringContextUtils.getBean(ISysBaseAPI.class);
            LoginUser userByName = bean.getUserByName(startUserId);
            String format = DateUtil.format(startTime, "yyyy-MM-dd");

            map.put("creatBy",userByName.getRealname());
            map.put("creatTime",format);
            bpmnTodoDTO.setTemplateCode(CommonConstant.BPM_SERVICE_NOTICE_PROCESS);
            bpmnTodoDTO.setData(map);
            bpmnTodoDTO.setMsgAbstract("有流程到达");

            bpmnTodoDTO.setTitle(bpmnTodoDTO.getProcessName()+"-"+userByName.getRealname()+"-"+DateUtil.format(startTime, "yyyy-MM-dd HH:mm:ss"));
            ISTodoBaseAPI todoBaseApi = SpringContextUtils.getBean(ISTodoBaseAPI.class);
            todoBaseApi.createBbmnTodoTask(bpmnTodoDTO);

            //发送通知
            ISysBaseAPI iSysBaseApi = SpringContextUtils.getBean(ISysBaseAPI.class);
            MessageDTO messageDTO = new MessageDTO();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, instance.getBusinessKey());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.BPM.getType());
            messageDTO.setData(map);
            messageDTO.setTitle(bpmnTodoDTO.getProcessName()+"-"+userByName.getRealname()+"-"+DateUtil.format(startTime, "yyyy-MM-dd HH:mm:ss"));
            messageDTO.setToUser(StrUtil.join(",", userNameList));
            messageDTO.setToAll(false);
            messageDTO.setTemplateCode(CommonConstant.BPM_SERVICE_NOTICE);
            ISysParamAPI sysParamAPI = SpringContextUtils.getBean(ISysParamAPI.class);
            SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.BPM_MESSAGE);
            messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
            messageDTO.setMsgAbstract("有流程到达");
            iSysBaseApi.sendTemplateMessage(messageDTO);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
