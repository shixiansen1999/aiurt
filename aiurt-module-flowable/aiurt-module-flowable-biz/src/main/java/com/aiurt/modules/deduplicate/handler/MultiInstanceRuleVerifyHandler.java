package com.aiurt.modules.deduplicate.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 加签
 * @author fgw
 */
@Slf4j
@Component
public class MultiInstanceRuleVerifyHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {

    private static final String IS_MUlTI_ASSIGN_TASK = "is_multi_assign_task";

    @Autowired
    private TaskService taskService;

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {

        // 通过流程变量区分是否是加签的用户
        Task task = context.getTask();

        if (log.isDebugEnabled()) {
            log.debug("审批去重，加签规则校验，任务id：{}， 节点id：{}", task.getId(), task.getTaskDefinitionKey());
        }

        Boolean isMultiAssignTask = taskService.getVariableLocal(task.getId(), IS_MUlTI_ASSIGN_TASK, Boolean.class);

        // 加签用户
        if (Objects.nonNull(isMultiAssignTask) && isMultiAssignTask) {
            context.setContinueChain(false);
            if (log.isDebugEnabled()) {
                log.debug("审批去重，加签规则校验，该用户是加签任务， 审批去重不生效，任务id：{}， 节点id：{}", task.getId(), task.getTaskDefinitionKey());
            }
        }
    }
}
