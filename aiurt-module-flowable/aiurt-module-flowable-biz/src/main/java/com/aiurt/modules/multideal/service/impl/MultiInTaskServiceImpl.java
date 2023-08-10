package com.aiurt.modules.multideal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.constant.FlowVariableConstant;
import com.aiurt.modules.common.enums.MultiApprovalRuleEnum;
import com.aiurt.modules.multideal.service.IMultiInTaskService;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.multideal.service.IMultiInstanceUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author fgw
 */
@Slf4j
@Service
public class MultiInTaskServiceImpl implements IMultiInTaskService {

    @Autowired
    private IActCustomTaskExtService taskExtService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IMultiInstanceUserService multiInstanceUserService;

    @Autowired
    private TaskService taskService;


    /**
     * 判断是否为多实例任务
     *
     * @param task
     * @return
     */
    @Override
    public Boolean isMultiInTask(Task task) {
        String nodeId = task.getTaskDefinitionKey();
        String definitionId = task.getProcessDefinitionId();
        ActCustomTaskExt actCustomTaskExt = taskExtService.getByProcessDefinitionIdAndTaskId(definitionId, nodeId);
        if (Objects.isNull(actCustomTaskExt)) {
            log.info("没有查询节点（{}）流转属性配置", nodeId);
            return false;
        }
        String userType = actCustomTaskExt.getUserType();
        if (StrUtil.isBlank(userType)) {
            return false;
        }
        MultiApprovalRuleEnum approvalRuleEnum = MultiApprovalRuleEnum.getByCode(userType);
        if (Objects.isNull(approvalRuleEnum)) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否多实例任务且不是多实例的最后一步
     * 是多实例任务且不是多实例的最后一步 返回true
     * 否则返回false
     *
     * @param task
     * @return
     */
    @Override
    public Boolean areMultiInTask(Task task) {
        String nodeId = task.getTaskDefinitionKey();
        String definitionId = task.getProcessDefinitionId();
        ActCustomTaskExt actCustomTaskExt = taskExtService.getByProcessDefinitionIdAndTaskId(definitionId, nodeId);
        if (Objects.isNull(actCustomTaskExt)) {
            log.info("没有查询节点（{}）流转属性配置", nodeId);
            return false;
        }
        String userType = actCustomTaskExt.getUserType();
        if (StrUtil.isBlank(userType)) {
            return false;
        }
        MultiApprovalRuleEnum approvalRuleEnum = MultiApprovalRuleEnum.getByCode(userType);
        if (Objects.isNull(approvalRuleEnum)) {
            return false;
        }

        switch (approvalRuleEnum) {
            case TASK_MULTI_INSTANCE_TYPE_2:
                return areSerialMultiInTask(task);
            case TASK_MULTI_INSTANCE_TYPE_3:
                return areParallelMultiInTask(task);
            default:
                return false;
        }
    }

    /**
     * 多实例-并行
     * @param task
     * @return
     */
    private Boolean areParallelMultiInTask(Task task) {
        String nodeId = task.getTaskDefinitionKey();
        String pInstanceId = task.getProcessInstanceId();
        String executionId = task.getExecutionId();
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(pInstanceId).
                activityId(nodeId).list();
        if (CollectionUtils.isEmpty(executions)){
            return false;
        }
        return executions.stream().filter(execution -> !StringUtils.equals(execution.getId(),executionId)).
                anyMatch(execution -> !execution.isEnded());
    }

    /**
     * 串行
     * @param task
     * @return
     */
    private Boolean areSerialMultiInTask(Task task) {
        String taskId = task.getId();
        String nodeId = task.getTaskDefinitionKey();
        List<String> currentUserList = multiInstanceUserService.getCurrentUserList(taskId);
        if (CollUtil.isEmpty(currentUserList) || currentUserList.size() == 1) {
            log.info("活动（{}）,节点（{}），是单实例", taskId, nodeId);
            return false;
        }

        Integer nrOfCompletedInstances = taskService.getVariableLocal(taskId, FlowVariableConstant.LOOP_COUNTER, Integer.class);
        if (nrOfCompletedInstances == null){
            log.info("活动（{}）,节点（{}），不是多单实例", taskId, nodeId);
            return false;
        }

        if (nrOfCompletedInstances < currentUserList.size() -1) {
            log.info("活动（{}）,节点（{}），是多实例且不是最后一个多实例任务", taskId, nodeId);
            return true;
        }
        return false;
    }


}
