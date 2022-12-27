package com.aiurt.modules.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.todo.dto.BpmnTodoDTO;
import com.aiurt.modules.user.service.IFlowUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
                    .taskDefinitionKey(taskDefinitionKey).orderByTaskCreateTime().desc().list();
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
            bpmnTodoDTO.setProcessDefinitionKey(taskEntity.getProcessDefinitionId());
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
            ISTodoBaseAPI todoBaseApi = SpringContextUtils.getBean(ISTodoBaseAPI.class);
            todoBaseApi.createBbmnTodoTask(bpmnTodoDTO);
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
