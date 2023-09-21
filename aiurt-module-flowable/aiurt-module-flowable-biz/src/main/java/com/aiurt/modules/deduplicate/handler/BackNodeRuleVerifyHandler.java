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
 * @author fgw
 */
@Slf4j
@Component
public class BackNodeRuleVerifyHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {


    private static final String REJECT_FIRST_USER_TASK = "reject_first_user_task" ;

    @Autowired
    private TaskService taskService;

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {
        log.info("审批去重， 驳回用户规则校验");
        // 通过流程变量区分是否是加签的用户
        Task task = context.getTask();

        Boolean isMultiAssignTask = taskService.getVariableLocal(task.getId(), REJECT_FIRST_USER_TASK, Boolean.class);

        // 加签用户
        if (Objects.nonNull(isMultiAssignTask) && isMultiAssignTask) {
            context.setContinueChain(false);
            log.info("该用户任务是驳回任务， 审批去重不生效");
        }
    }
}
