package com.aiurt.modules.deduplicate.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomModelExtService;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>构造上下文</p>
 * @author fgw
 */
@Slf4j
@Component
public class BuildDeduplicateContextHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {

    @Autowired
    private IActCustomModelExtService modelExtService;

    @Autowired
    private IActCustomTaskExtService taskExtService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;



    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {
        if (log.isDebugEnabled()) {
            log.debug("审批去重，开始构建审批去重上下文");
        }

        // 当前任务
        Task task = context.getTask();
        // 流程实例id
        String processInstanceId = task.getProcessInstanceId();
        // 流程定义id
        String definitionId = task.getProcessDefinitionId();
        // 任务节点id
        String taskDefinitionKey = task.getTaskDefinitionKey();

        // 流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 任务扩展属性
        ActCustomTaskExt actCustomTaskExt = taskExtService.getByProcessDefinitionIdAndTaskId(definitionId, taskDefinitionKey);
        //
        ActCustomModelExt actCustomModelExt = modelExtService.getByProcessDefinitionId(definitionId);
        // 执行实行
        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        //
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        // 过滤没有审批的历史数据
        list = list.stream().filter(historicTaskInstance -> (Objects.nonNull(historicTaskInstance.getClaimTime())
                        || Objects.isNull(historicTaskInstance.getEndTime()))).collect(Collectors.toList());

        context.setActCustomModelExt(actCustomModelExt);
        context.setActCustomTaskExt(actCustomTaskExt);
        context.setProcessInstance(processInstance);
        context.setHistoricTaskInstanceList(list);
        context.setExecution(execution);

        if (log.isDebugEnabled()) {
            log.debug("审批去重，结束构建审批去重上下文");
        }
    }
}
