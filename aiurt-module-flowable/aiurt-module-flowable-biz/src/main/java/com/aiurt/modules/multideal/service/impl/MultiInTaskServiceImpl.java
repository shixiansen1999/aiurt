package com.aiurt.modules.multideal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.enums.MultiApprovalRuleEnum;
import com.aiurt.modules.multideal.service.IMultiInTaskService;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
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
     * 多实例
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

    private Boolean areSerialMultiInTask(Task task) {
        String taskId = task.getId();
        String nodeId = task.getTaskDefinitionKey();
        return false;
    }


}
