package com.aiurt.modules.complete.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.complete.dto.CompleteTaskContext;
import com.aiurt.modules.complete.dto.FlowCompleteReqDTO;
import com.aiurt.modules.flow.dto.NextNodeUserDTO;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.multideal.dto.MultiDealDTO;
import com.aiurt.modules.multideal.service.IMultiInTaskService;
import com.aiurt.modules.multideal.service.IMultiInstanceDealService;
import com.aiurt.modules.user.service.IActCustomUserService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Slf4j
@Service
public class CommonFlowTaskCompleteServiceImpl extends AbsFlowCompleteServiceImpl {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IActCustomTaskExtService taskExtService;

    @Autowired
    private IMultiInTaskService multiInTaskService;

    @Autowired
    private FlowElementUtil flowElementUtil;

    @Autowired
    private IActCustomUserService customUserService;

    @Autowired
    private IMultiInstanceDealService multiInstanceDealService;



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

        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();

        ActCustomTaskExt customTaskExt = taskExtService.getByProcessDefinitionIdAndTaskId(task.getProcessDefinitionId(), task.getTaskDefinitionKey());

        taskContext.setCurrentTask(task);
        taskContext.setProcessInstance(processInstance);
        taskContext.setExecutionEntity((ExecutionEntity) execution);

        // 判断是否多实例最后一个， 如果不是最后一个不需要提交
        Boolean multiInTask = multiInTaskService.areMultiInTask(task);
        // 自动选人 1, 0 否
        Integer isAutoSelect = customTaskExt.getIsAutoSelect();
        // 办理规则 如果办理规则为空则，就是旧版流程选人，不需要处理
        String userType = customTaskExt.getUserType();
        // multiInTask当前活动是多少实例，且不是多实例的最后一个活动，不设置下一步多实例办理人
        if (multiInTask) {
            log.info("当前活动是多少实例，且不是多实例的最后一个活动，不设置下一步多实例办理人");
            return;
        }
        // 多实例，自动选人则构造下一步节点以及下一个节点的数据,
        if (StrUtil.isNotBlank(userType) && Objects.nonNull(isAutoSelect) && isAutoSelect == 1) {
            FlowElement flowElement = flowElementUtil.getFlowElement(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            List<FlowElement> targetFlowElement = flowElementUtil.getTargetFlowElement(execution, flowElement, flowCompleteReqDTO.getBusData());
            List<NextNodeUserDTO> nodeUserDTOList = targetFlowElement.stream().map(element -> {
                NextNodeUserDTO nextNodeUserDTO = new NextNodeUserDTO();
                nextNodeUserDTO.setNodeId(element.getId());
                List<String> userList = customUserService.getUserByTaskInfo(task.getProcessDefinitionId(), element.getId(), "0");
                nextNodeUserDTO.setApprover(userList);
                return nextNodeUserDTO;
            }).collect(Collectors.toList());
            flowCompleteReqDTO.setNextNodeUserParam(nodeUserDTOList);
        }

        // 构建流程变量
        Map<String, Object> variableData = flowCompleteReqDTO.getVariableData();
        if (Objects.isNull(variableData)) {
            variableData = new HashMap<>(16);
        }
        Map<String, Object> variables = flowElementUtil.getVariables(flowCompleteReqDTO.getBusData(), processInstanceId);
        variableData.putAll(variables);
        taskContext.setVariableData(variableData);
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
        //多实例任务处理
        Task currentTask = taskContext.getCurrentTask();
        FlowCompleteReqDTO flowCompleteReqDTO = taskContext.getFlowCompleteReqDTO();
        MultiDealDTO multiDealDTO = new MultiDealDTO(flowCompleteReqDTO);
        log.info("多实例任务处理,活动（{}）",currentTask.getId());
        multiInstanceDealService.multiInstanceDeal(multiDealDTO);

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
        Task currentTask = taskContext.getCurrentTask();
        Map<String, Object> variableData = taskContext.getVariableData();
        taskService.complete(currentTask.getId(), variableData);
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
