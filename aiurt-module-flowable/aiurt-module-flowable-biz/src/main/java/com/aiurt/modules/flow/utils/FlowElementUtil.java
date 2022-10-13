package com.aiurt.modules.flow.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.aiurt.modules.manage.service.IActCustomVersionService;
import com.aiurt.modules.modeler.dto.OperationList;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.utils.ReflectionService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 流程元素工具
 * @author fgw
 */
@Slf4j
@Component
public class FlowElementUtil {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private IActCustomVersionService actCustomVersionService;

    @Autowired
    private IActCustomModelInfoService modelInfoService;

    @Autowired
    private ReflectionService reflectionService;

    @Autowired
    private IActCustomTaskExtService customTaskExtService;

    @Autowired
    private RuntimeService runtimeService;

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

        // 获取model id
        LambdaQueryWrapper<ActCustomModelInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActCustomModelInfo::getModelKey, modelKey).last("limit 1");
        ActCustomModelInfo customModelInfo = modelInfoService.getOne(wrapper);
        if (Objects.isNull(customModelInfo)) {
             throw new AiurtBootException(AiurtErrorEnum.FLOW_DEFINITION_NOT_FOUND.getMessage());
        }

        // 获取主版本的流程定义.
        LambdaQueryWrapper<ActCustomVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomVersion::getModelId, customModelInfo.getModelId())
                .eq(ActCustomVersion::getMainVersion, FlowConstant.MAIN_VERSION_1).last("limit 1");
        ActCustomVersion customVersion = actCustomVersionService.getOne(queryWrapper);

        if (Objects.isNull(customVersion)) {
            throw new AiurtBootException(AiurtErrorEnum.FLOW_DEFINITION_NOT_FOUND.getCode(), AiurtErrorEnum.FLOW_DEFINITION_NOT_FOUND.getMessage());
        }

        BpmnModel model = repositoryService.getBpmnModel(customVersion.getProcessDefinitionId());

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
        ProcessDefinition processDefinition = getProcessDefinition(processDefinitionKey);

        return Result.OK(processDefinition);

       /* if (CollUtil.isNotEmpty(processList)) {
            for (ProcessDefinition processDefinition : processList) {
                if (ObjectUtil.isNotEmpty(processDefinition)) {
                    ActCustomVersion actCustomVersions = actCustomVersionService.getBaseMapper().selectOne(
                            new LambdaQueryWrapper<ActCustomVersion>()
                                    .eq(ActCustomVersion::getProcessDefinitionId, processDefinition.getId())
                                    .eq(ActCustomVersion::getMainVersion, FlowConstant.MAIN_VERSION_1));
                    if (ObjectUtil.isNotEmpty(actCustomVersions)) {
                        return Result.OK(processDefinition);
                    }
                }
            }
        }*/
    }

    public ProcessDefinition getProcessDefinition(String processDefinitionKey) {
        LambdaQueryWrapper<ActCustomModelInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActCustomModelInfo::getModelKey, processDefinitionKey).last("limit 1");
        ActCustomModelInfo customModelInfo = modelInfoService.getOne(wrapper);
        if (Objects.isNull(customModelInfo)) {
            throw new AiurtBootException(AiurtErrorEnum.FLOW_DEFINITION_NOT_FOUND.getMessage());
        }

        // 获取主版本的流程定义.
        LambdaQueryWrapper<ActCustomVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomVersion::getModelId, customModelInfo.getModelId())
                .eq(ActCustomVersion::getMainVersion, FlowConstant.MAIN_VERSION_1).last("limit 1");
        ActCustomVersion customVersion = actCustomVersionService.getOne(queryWrapper);

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(customVersion.getProcessDefinitionId()).singleResult();
        return processDefinition;
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


    /**
     * 保存业务数据
     *
     * @param pProcessDefinitionId
     * @param taskId
     * @return
     */
    public Object saveBusData(String pProcessDefinitionId, String taskId,  Map<String, Object> busData) {
        log.info("处理中间业务数据:pProcessDefinitionId->{}, taskId->{}, busData->{}", pProcessDefinitionId, taskId, busData);
        if (Objects.isNull(busData)) {
            return "";
        }
        List<ActCustomTaskExt> actCustomTaskExts = customTaskExtService.getBaseMapper().selectList(
                new LambdaQueryWrapper<ActCustomTaskExt>()
                        .eq(ActCustomTaskExt::getProcessDefinitionId, pProcessDefinitionId)
                        .eq(ActCustomTaskExt::getTaskId, taskId));
        // 数据结构_转为驼峰
        Map<String, Object> data = new HashMap<>(16);
        busData.keySet().stream().forEach(key->{
            String s = StrUtil.toCamelCase(key);
            data.put(s, busData.get(key));
        });

        // 是否动态表单
        if (CollUtil.isNotEmpty(actCustomTaskExts)) {
            JSONObject jsonObject = JSONObject.parseObject(actCustomTaskExts.get(0).getFormJson());
            //log.info("更新或保存业务接口:->{}", );
            if (ObjectUtil.isNotEmpty(jsonObject)) {
                String service = jsonObject.getString("service");
                if (StrUtil.isBlank(service)) {
                    service = jsonObject.getString("className");
                }
                List<String> className = StrUtil.split(service, '.');
                try {
                    if (CollUtil.isNotEmpty(className)) {
                        return reflectionService.invokeService(className.get(0), className.get(1), data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 挂起流程定义对象
     * @param processDefinitionId 流程定义Id
     */
    public void suspendProcessDefinition(String  processDefinitionId) {
        repositoryService.suspendProcessDefinitionById(processDefinitionId);
    }

    /**
     * 激活流程定义对象
     * @param processDefinitionId 流程定义Id
     */
    public void activateProcessDefinition(String processDefinitionId) {
        repositoryService.activateProcessDefinitionById(processDefinitionId);
    }


    /**
     * 为流程实例设置BusinessKey。
     *
     * @param processInstanceId 流程实例Id。
     * @param dataId            通常为主表的主键Id。
     */
    public void setBusinessKeyForProcessInstance(String processInstanceId, Object dataId) {
        runtimeService.updateBusinessKey(processInstanceId, dataId.toString());
    }
}
