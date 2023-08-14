package com.aiurt.modules.complete.service.impl;

import com.aiurt.modules.complete.dto.CompleteTaskContext;
import com.aiurt.modules.complete.dto.FlowCompleteReqDTO;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fgw
 */
@Service
public class CommonFlowTaskCompleteServiceImpl extends AbsFlowCompleteServiceImpl {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IActCustomTaskExtService taskExtService;

    /**
     * 始前处理
     *
     * @param taskContext
     * @Description: preDeal
     * @author fgw
     */
    @Override
    public void preDeal(CompleteTaskContext taskContext) {
        super.preDeal(taskContext);
    }

    /**
     * 构建上下文环境。获取当前任务、节点、流程等信息。
     *
     * @param taskContext
     * @Description: buildTaskContext
     * @author fgw
     */
    @Override
    public void buildTaskContext(CompleteTaskContext taskContext) {
        FlowCompleteReqDTO flowCompleteReqDTO = taskContext.getFlowCompleteReqDTO();
        String taskId = flowCompleteReqDTO.getTaskId();
        String processInstanceId = flowCompleteReqDTO.getProcessInstanceId();

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        Task task = taskService.createTaskQuery().taskId(taskId).processInstanceId(processInstanceId).singleResult();

        ActCustomTaskExt customTaskExt = taskExtService.getByProcessDefinitionIdAndTaskId(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        // 自动选人
        Integer isAutoSelect = customTaskExt.getIsAutoSelect();
        // 办理规则
        String userType = customTaskExt.getUserType();
        // 如果办理规则为空则，就是旧版流程选人，不需要处理

        // 怎么兼容以前的数据
        taskContext.setCurrentTask(task);
        taskContext.setProcessInstance(processInstance);

        // 是否多实例， 是否自动选人，自动选人则构造下一步节点以及下一个节点的数据
    }

    /**
     * 处理会签任务
     *
     * @param taskContext
     * @Description: dealSignTask
     * @author fgw
     */
    @Override
    public void dealSignTask(CompleteTaskContext taskContext) {
        super.dealSignTask(taskContext);
    }

    /**
     * 在任务执行完前处理下一个节点。设置下一个节点参数。
     *
     * @param taskContext
     * @Description: dealNextNodeBeforeComplete
     * @author fgw
     */
    @Override
    public void dealNextNodeBeforeComplete(CompleteTaskContext taskContext) {
        // 判断是否是自动提交的，如果不是自动提交则需要获取下一个节点的人员信息,

    }

    /**
     * 执行complete操作
     *
     * @param taskContext
     * @Description: dealComplete
     * @author fgw
     */
    @Override
    public void dealComplete(CompleteTaskContext taskContext) {
        super.dealComplete(taskContext);
    }

    /**
     * 完成后处理事件
     *
     * @param taskContext
     * @Description: afterDeal
     * @author fgw
     */
    @Override
    public void afterDeal(CompleteTaskContext taskContext) {
        super.afterDeal(taskContext);
    }
}
