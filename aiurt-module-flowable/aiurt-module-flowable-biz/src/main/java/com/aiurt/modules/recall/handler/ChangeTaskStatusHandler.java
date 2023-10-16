package com.aiurt.modules.recall.handler;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/9/14
 * @time: 15:53
 */

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.handler.BackNodeRuleVerifyHandler;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.aiurt.modules.flow.service.IActCustomTaskCommentService;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.modeler.service.IActCustomModelExtService;
import com.aiurt.modules.recall.context.FlowRecallContext;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.service.IActCustomRemindRecordService;
import liquibase.pro.packaged.A;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023-09-14 15:53
 */
@Component
public class ChangeTaskStatusHandler extends AbstractFlowHandler<FlowRecallContext> {
    @Resource
    private FlowElementUtil flowElementUtil;

    @Resource
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;
    @Override
    public void handle(FlowRecallContext context) {
        //撤回之后将我的已办任务变成我的待办
        String processInstanceId = context.getProcessInstanceId();
        HistoricProcessInstance processInstance = context.getProcessInstance();
        String processDefinitionId = processInstance.getProcessDefinitionId();
        //获取流程所有节点

        // fixby fugaowei 在并行网关，多实例中撤回有条数据的问题或者 原因分析（https://blog.csdn.net/weixin_44663675/article/details/125839004）
        List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstanceId).list();
        List<String> exctutionIds = executions.stream().map(Execution::getId).collect(Collectors.toList());


        //获取流程发起节点
        FlowElement startEvent = flowElementUtil.getFirstUserTaskByDefinitionId(processDefinitionId);
        String startElementId = null;
        if (ObjectUtil.isNotEmpty(startEvent)) {
             startElementId = startEvent.getId();
        }
        // 增加变量
        Map<String, Object> localVariableMap = new HashMap<>();
        localVariableMap.put(BackNodeRuleVerifyHandler.REJECT_FIRST_USER_TASK, true);
        //将所有节点撤回到开始节点
        runtimeService.createChangeActivityStateBuilder().processInstanceId(processInstanceId)
                .moveExecutionsToSingleActivityId(exctutionIds, startElementId)
                .localVariables(startElementId, localVariableMap).changeState();
    }
}
