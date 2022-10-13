package com.aiurt.modules.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.user.service.IFlowUserService;
import org.apache.shiro.SecurityUtils;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl;
import org.flowable.engine.ProcessEngines;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        String taskId = taskEntity.getId();
        String processDefinitionId = taskEntity.getProcessDefinitionId();

        IActCustomTaskExtService taskExtService = SpringContextUtils.getBean(IActCustomTaskExtService.class);

        ActCustomTaskExt taskExt = taskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, taskId);

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //
        if (Objects.isNull(taskExt) && Objects.nonNull(loginUser)) {
            // 设置当前登录人员
            ProcessEngines.getDefaultProcessEngine().getTaskService().setAssignee(taskId, loginUser.getUsername());
        }

        String groupType = taskExt.getGroupType();
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
                        .getVariable(taskEntity.getProcessInstanceId(), dynamicVariable, String.class);
                if (StrUtil.isNotBlank(variable)) {
                    userNameList = flowUserService.getUserName(variable);
                }
                break;
            // 流程发起人
            default:
                String initiator = ProcessEngines.getDefaultProcessEngine().getRuntimeService()
                        .getVariable(taskEntity.getProcessInstanceId(), FlowConstant.PROC_INSTANCE_INITIATOR_VAR, String.class);
                userNameList.add(initiator);
                break;
        }

        if (CollectionUtil.isEmpty(userNameList)) {
            String initiator = ProcessEngines.getDefaultProcessEngine().getRuntimeService()
                    .getVariable(taskEntity.getProcessInstanceId(), FlowConstant.PROC_INSTANCE_INITIATOR_VAR, String.class);
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
