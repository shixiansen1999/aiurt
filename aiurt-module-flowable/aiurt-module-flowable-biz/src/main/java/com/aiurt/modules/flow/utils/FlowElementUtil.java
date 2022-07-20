package com.aiurt.modules.flow.utils;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
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


    /**
     * 获取第一个用户节点, 最近的一个版本
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
     *  获取流程开始节点
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
     * @param modelKey  流程模型key
     * @return  FlowElement
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
     *  获取流程开始节点
     * @return
     */
    public FlowElement getStartFlowNodeByDefinitionId(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        Collection<FlowElement> flowElements = null;
        FlowElement startFlowElement = null;
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof StartEvent) {
                startFlowElement = flowElement;
            }
        }
        return startFlowElement;
    }




}
