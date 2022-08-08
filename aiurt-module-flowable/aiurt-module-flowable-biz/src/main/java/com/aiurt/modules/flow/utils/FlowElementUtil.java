package com.aiurt.modules.flow.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.aiurt.modules.manage.service.IActCustomVersionService;
import com.aiurt.modules.modeler.dto.OperationList;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 流程元素工具
 */
@Component
public class FlowElementUtil {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private IActCustomVersionService actCustomVersionService;


    /**
     * 获取第一个用户节点, 最近的一个版本
     *
     * @param modelKey 流程模型key
     * @return
     */
    public UserTask getFirstUserTaskByModelKey(String modelKey) {

        // 开始节点
        FlowElement flowElement = getStartFlowNodeByModelKey(modelKey);

        // 判断
        if (Objects.isNull(flowElement)) {
            throw new AiurtBootException("");
        }

        // 获取用户节点
        FlowNode startFlowNode = (FlowNode) flowElement;
        UserTask userTask = null;
        // 遍历
        List<SequenceFlow> outgoingFlows = startFlowNode.getOutgoingFlows();
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            FlowElement targetFlowElement = outgoingFlow.getTargetFlowElement();
            if (targetFlowElement instanceof UserTask) {
                userTask = (UserTask) targetFlowElement;
            }
        }

        return userTask;
    }

    /**
     * 获取流程开始节点
     *
     * @return
     */
    public FlowElement getStartFlowNodeByModelKey(String modelKey) {
        Collection<FlowElement> flowElements = getFlowElementsByModelKey(modelKey);
        FlowElement startFlowElement = null;
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof StartEvent) {
                startFlowElement = flowElement;
            }
        }
        return startFlowElement;
    }

    /**
     * 获取流程所有的元素
     *
     * @param modelKey 流程模型key
     * @return FlowElement
     */
    private Collection<FlowElement> getFlowElementsByModelKey(String modelKey) {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey(modelKey).singleResult();

        if (Objects.isNull(processDefinition)) {
            throw new AiurtBootException(AiurtErrorEnum.FLOW_DEFINITION_NOT_FOUND.getCode(), AiurtErrorEnum.FLOW_DEFINITION_NOT_FOUND.getMessage());
        }

        BpmnModel model = repositoryService.getBpmnModel(processDefinition.getId());

        Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();

        return flowElements;
    }


    /**
     * @param processDefinitionId 流程定义id
     * @return
     */
    public UserTask getFirstUserTaskByDefinitionId(String processDefinitionId) {


        return null;
    }


    /**
     * 获取流程开始节点
     *
     * @return
     */
    public FlowElement getStartFlowNodeByDefinitionId(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        StartEvent startEvent = bpmnModel.getMainProcess().findFlowElementsOfType(StartEvent.class, false).get(0);

        return startEvent;
    }


    /**
     * 验证并获取流程对象。(根据key查找当前流程的主版本)
     *
     * @param processDefinitionKey 流程引擎的流程定义标识。
     * @return 流程对象。
     */
    public Result<ProcessDefinition> verifyAndGetFlowEntry(String processDefinitionKey) {
        List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .list();

        if (CollUtil.isNotEmpty(processList)) {
            for (ProcessDefinition processDefinition : processList) {
                if (ObjectUtil.isNotEmpty(processDefinition)) {
                    ActCustomVersion actCustomVersions = actCustomVersionService.getBaseMapper().selectById(
                            new LambdaQueryWrapper<ActCustomVersion>()
                                    .eq(ActCustomVersion::getProcessDefinitionId, processDefinition.getId())
                                    .eq(ActCustomVersion::getMainVersion, FlowConstant.MAIN_VERSION_1));
                    if (ObjectUtil.isNotEmpty(actCustomVersions)) {
                        return Result.OK(processDefinition);
                    }
                }
            }
        }

        return Result.error(AiurtErrorEnum.FLOW_DEFINITION_NOT_FOUND.getMessage());
    }

    /**
     * 根据节点定义id 获取每个节点的按钮信息
     * @return
     */
    public OperationList getElementOperation(UserTask userTask) {
        return null;
    }

    /**
     * 根据流程定义id以及节点id 获取流程元素节点
     * @param processDefinitionId
     * @param flowElementId
     * @return
     */
    public FlowElement getFlowElement(String processDefinitionId, String flowElementId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        if (Objects.isNull(bpmnModel)) {
            throw new AiurtBootException("");
        }
        FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(flowElementId);

        return flowElement;
    }

    /**
     * 根据流程定义id，获取结束节点
     * @param processDefinitionId
     * @return
     */
    public EndEvent getEndEvent(String processDefinitionId) {

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        EndEvent endEvent = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class, false).get(0);

        return endEvent;
    }
}
