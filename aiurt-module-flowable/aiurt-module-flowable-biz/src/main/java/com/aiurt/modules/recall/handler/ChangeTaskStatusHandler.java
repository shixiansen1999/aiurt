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
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023-09-14 15:53
 */
@Component
public class ChangeTaskStatusHandler extends AbstractFlowHandler<FlowRecallContext> {
    @Resource
    private HistoryService historyService;

    @Resource
    private IActCustomModelExtService modelExtService;

    @Resource
    private TaskService taskService;

    @Resource
    private IActCustomTaskCommentService actCustomTaskCommentService;

    @Resource
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;
    @Override
    public void handle(FlowRecallContext context) {
        //撤回之后将我的已办任务变成我的待办
        String processInstanceId = context.getProcessInstanceId();
         //获取流程所有节点
        List<String> activityIdsToMove = new ArrayList<>();
        List<Task> list = taskService.createTaskQuery().active().list();
        for (Task task : list) {
            activityIdsToMove.add(task.getId());
        }
        //获取流程发起节点
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        StartEvent startEvent = bpmnModel.getMainProcess().findFlowElementsOfType(StartEvent.class, false).get(0);
        String startElementId = null;
        if (ObjectUtil.isNotEmpty(startEvent)) {
             startElementId = startEvent.getId();
        }
        //将所有节点撤回到开始节点
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveActivityIdsToSingleActivityId(activityIdsToMove,startElementId)
                .changeState();
    }
}
