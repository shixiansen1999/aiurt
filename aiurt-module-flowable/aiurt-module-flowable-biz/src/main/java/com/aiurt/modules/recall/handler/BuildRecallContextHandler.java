package com.aiurt.modules.recall.handler;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.modeler.service.IActCustomModelExtService;
import com.aiurt.modules.recall.context.FlowRecallContext;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author fgw
 */
@Component
public class BuildRecallContextHandler<T extends FlowRecallContext> extends AbstractFlowHandler<T> {
    @Resource
    private HistoryService historyService;

    @Resource
    private IActCustomModelExtService modelExtService;

    @Resource
    private TaskService taskService;

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {
        // 具体逻辑实现
        String processInstanceId = context.getProcessInstanceId();

        // 流程实例(我的已办)
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if (Objects.isNull(historicProcessInstance)) {
            throw new AiurtBootException(AiurtErrorEnum.PROCESS_INSTANCE_NOT_FOUND.getCode(), AiurtErrorEnum.FLOW_DEFINITION_NOT_FOUND.getMessage());
        }
        //流程模型配置
        ActCustomModelExt modelExt = modelExtService.getByProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());

        List<Task> taskList = taskService.createTaskQuery().active().processInstanceId(processInstanceId).list();

        context.setActCustomModelExt(modelExt);
        context.setProcessInstance(historicProcessInstance);
        context.setTaskList(taskList);

        if (Objects.isNull(modelExt)) {
            // 流程属性没有，不执行之后的处理器
            context.setContinueChain(false);
        }


    }
}
