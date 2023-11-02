package com.aiurt.modules.deduplicate.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author fgw
 */
@Slf4j
@Component
public class BackNodeRuleVerifyHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {
    public static final String REJECT_FIRST_USER_TASK = "reject_first_user_task" ;

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {

        // 通过流程变量区分是否是加签的用户
        Task task = context.getTask();
        ExecutionEntity execution = (ExecutionEntity) context.getExecution();
        Object isBackNodeTaskObj = execution.getVariableLocal(REJECT_FIRST_USER_TASK);

        if (log.isDebugEnabled()) {
            log.debug("审批去重，回退，撤回规则校验，任务id：{}， 节点id：{}", task.getId(), task.getTaskDefinitionKey());
        }
        Boolean isBackNodeTask = Boolean.FALSE;
        if (Objects.nonNull(isBackNodeTaskObj) && isBackNodeTaskObj instanceof Boolean) {
            isBackNodeTask = (Boolean) isBackNodeTaskObj;
        }
        // 加签用户
        if (Boolean.TRUE.equals(isBackNodeTask)) {
            context.setContinueChain(false);
            if (log.isDebugEnabled()) {
                log.debug("审批去重，该用户任务是驳回任务， 审批去重不生效，任务id：{}， 节点id：{}", task.getId(), task.getTaskDefinitionKey());
            }
        }
    }
}
