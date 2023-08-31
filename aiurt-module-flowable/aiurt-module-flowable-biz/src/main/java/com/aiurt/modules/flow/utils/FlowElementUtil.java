package com.aiurt.modules.flow.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.cmd.ConditionExpressionCmd;
import com.aiurt.modules.cmd.ConditionExpressionV2Cmd;
import com.aiurt.modules.common.constant.FlowCustomVariableConstant;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.aiurt.modules.manage.service.IActCustomVersionService;
import com.aiurt.modules.modeler.dto.OperationList;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.entity.ActCustomVariable;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.modeler.service.IActCustomVariableService;
import com.aiurt.modules.utils.ReflectionService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.engine.*;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
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
    private IActCustomVersionService actCustomVersionService;

    @Autowired
    private IActCustomModelInfoService modelInfoService;

    @Autowired
    private ReflectionService reflectionService;

    @Autowired
    private IActCustomTaskExtService customTaskExtService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IActCustomVariableService variableService;

    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Autowired
    private ManagementService managementService;

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
            throw new AiurtBootException("流程模型不存在");
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
        ActCustomVersion customVersion = getFlowMainVersion(modelKey);

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
        FlowElement flowElement = this.getStartFlowNodeByDefinitionId(processDefinitionId);

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
    }

    public ProcessDefinition getProcessDefinition(String processDefinitionKey) {
        ActCustomVersion customVersion = getFlowMainVersion(processDefinitionKey);

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(customVersion.getProcessDefinitionId()).singleResult();
        return processDefinition;
    }

    /**
     * 查询流程主版本
     * @param processDefinitionKey
     * @return
     */
    private ActCustomVersion getFlowMainVersion(String processDefinitionKey) {
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
        return actCustomVersionService.getOne(queryWrapper);
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
            log.info("更新或保存业务接口:->{}", jsonObject);
            if (ObjectUtil.isNotEmpty(jsonObject)) {
                String service = jsonObject.getString("service");
                if (StrUtil.isBlank(service)) {
                    service = jsonObject.getString("className");
                }
                List<String> className = StrUtil.split(service, '.');
                try {
                    if (CollUtil.isNotEmpty(className)) {
                        return reflectionService.proxy(service, data);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw e;
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

    /**
     * 判断是否为驳回到第一个节点
     * @param processDefinitionId
     * @param taskDefinitionKey
     * @param processInstanceId
     * @return
     */
    public boolean isBackToFirstTask(String processDefinitionId, String taskDefinitionKey, String processInstanceId) {
        UserTask userTask = getFirstUserTaskByDefinitionId(processDefinitionId);
        if (Objects.isNull(userTask)) {
            return false;
        }

        if (!StrUtil.equalsAnyIgnoreCase(userTask.getId(), taskDefinitionKey)) {
            return false;
        }
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey).finished().orderByTaskCreateTime().desc().list();

        if (CollUtil.isNotEmpty(list)) {
            return true;
        }

        return false;
    }


    /**
     * 获取下一个代办的节点
     * @param execution
     * @param sourceFlowElement
     * @return
     */
    public List<FlowElement> getTargetFlowElement(Execution execution, FlowElement sourceFlowElement) {
        List<FlowElement> flowElementList = new ArrayList<>();
        getTargetFlowElement(execution, sourceFlowElement, flowElementList, null);
        return flowElementList;
    }




    /**
     * 获取下一个代办的节点
     * @param execution 执行实例
     * @param sourceFlowElement 源节点
     * @return
     */
    public List<FlowElement> getTargetFlowElement(Execution execution, FlowElement sourceFlowElement, Map<String, Object> busData) {
        List<FlowElement> flowElementList = new ArrayList<>();
        Map<String, Object> variables = getVariables(busData, execution.getProcessInstanceId());
        getTargetFlowElement(execution, sourceFlowElement, flowElementList, variables);
        return flowElementList;
    }

    /**
     * 根据流程标识获取下一个代办的节点
     * @param modelKey 流程标识
     * @param sourceFlowElement 源节点
     * @return
     */
    public List<FlowElement> getTargetFlowElement(String modelKey, FlowElement sourceFlowElement, Map<String, Object> busData) {
        List<FlowElement> flowElementList = new ArrayList<>();
        Map<String, Object> variables = getVariablesByModelKey(busData, modelKey);
        getTargetFlowElement(sourceFlowElement, flowElementList, variables);
        return flowElementList;
    }




    /**
     * 获取下一个代办的节点, 包括结束节点
     * @param execution 执行实例
     * @param sourceFlowElement  源节点
     * @param flowElementList 结果集
     * @return
     */
    public void getTargetFlowElement(Execution execution, FlowElement sourceFlowElement, List<FlowElement> flowElementList, Map<String, Object> variables) {
        //遇到下一个节点是UserTask就返回
        if (sourceFlowElement instanceof FlowNode) {
            //当前节点必须是FlowNode才做处理，比如UserTask或者GateWay
            FlowNode thisFlowNode = (FlowNode) sourceFlowElement;
            if (thisFlowNode.getOutgoingFlows().size() == 1) {
                //如果只有一条连接线，直接找这条连接线的出口节点，然后继续递归获得接下来的节点
                SequenceFlow sequenceFlow = thisFlowNode.getOutgoingFlows().get(0);
                FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                if (targetFlowElement instanceof UserTask || targetFlowElement instanceof EndEvent) {
                    flowElementList.add(targetFlowElement);
                } else {
                    getTargetFlowElement(execution, targetFlowElement, flowElementList, variables);
                }
            } else if (thisFlowNode.getOutgoingFlows().size() > 1) {
                //如果有多条连接线，遍历连接线，找出一个连接线条件执行为True的，获得它的出口节点
                for (SequenceFlow sequenceFlow : thisFlowNode.getOutgoingFlows()) {
                    boolean result = true;
                    if (StrUtil.isNotBlank(sequenceFlow.getConditionExpression())) {
                        //计算连接线上的表达式
                        CommandExecutor commandExecutor = ProcessEngines.getDefaultProcessEngine().getProcessEngineConfiguration().getCommandExecutor();

                        result = commandExecutor.execute(new ConditionExpressionCmd((ExecutionEntity) execution, sequenceFlow.getConditionExpression(), variables));
                    }
                    if (result) {
                        FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                        if (targetFlowElement instanceof UserTask || targetFlowElement instanceof EndEvent) {
                            flowElementList.add(targetFlowElement);
                        } else {
                            getTargetFlowElement(execution, targetFlowElement, flowElementList, variables);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取下一个代办的节点, 没有流程实例
     * @param sourceFlowElement 源节点
     * @param flowElementList  结果集
     * @return
     */
    public void getTargetFlowElement(FlowElement sourceFlowElement, List<FlowElement> flowElementList, Map<String, Object> variables) {
        //遇到下一个节点是UserTask就返回
        if (sourceFlowElement instanceof FlowNode) {
            //当前节点必须是FlowNode才做处理，比如UserTask或者GateWay
            FlowNode thisFlowNode = (FlowNode) sourceFlowElement;
            if (thisFlowNode.getOutgoingFlows().size() == 1) {
                //如果只有一条连接线，直接找这条连接线的出口节点，然后继续递归获得接下来的节点
                SequenceFlow sequenceFlow = thisFlowNode.getOutgoingFlows().get(0);
                FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                if (targetFlowElement instanceof UserTask) {
                    flowElementList.add(targetFlowElement);
                }  else {
                    getTargetFlowElement(targetFlowElement, flowElementList, variables);
                }
            } else if (thisFlowNode.getOutgoingFlows().size() > 1) {
                //如果有多条连接线，遍历连接线，找出一个连接线条件执行为True的，获得它的出口节点
                for (SequenceFlow sequenceFlow : thisFlowNode.getOutgoingFlows()) {
                    boolean result = true;
                    if (StrUtil.isNotBlank(sequenceFlow.getConditionExpression())) {
                        variables.put("day", 3);
                        //计算连接线上的表达式
                        result = managementService.executeCommand(new ConditionExpressionV2Cmd(sequenceFlow.getConditionExpression(), variables));
                    }
                    if (result) {
                        FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                        if (targetFlowElement instanceof UserTask) {
                            flowElementList.add(targetFlowElement);
                        } else {
                            getTargetFlowElement(targetFlowElement, flowElementList, variables);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param activityId  id of the multi-instance activity (id attribute in the BPMN XML)
     * @param  parentExecutionId  can be the process instance id
     * @param assigneeList username
     */
    public void addMultiInstanceExecution(String activityId, String parentExecutionId, List<String> assigneeList) {
        // 设置多实例
    }

    /**
     * 构建流程变量，防止页面的数据太多
     * @param busData
     * @param processInstanceId
     * @return
     */
    public Map<String, Object> getVariables(Map<String, Object> busData, String processInstanceId) {
        Map<String, Object> variableData = new HashMap<>(16);

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if (Objects.isNull(processInstance)) {
            throw new AiurtBootException(AiurtErrorEnum.PROCESS_INSTANCE_NOT_FOUND.getCode(), AiurtErrorEnum.PROCESS_INSTANCE_NOT_FOUND.getMessage());
        }

        // 流程key
        String processDefinitionKey = processInstance.getProcessDefinitionKey();

        // 流程模板信息
        setVariableData(busData, variableData, processDefinitionKey);

        // 流程发起人
        String startUserId = processInstance.getStartUserId();

        // 发起人角色， 部门， 岗位
        LoginUser user = sysBaseApi.getUserByName(startUserId);
        variableData.put(FlowCustomVariableConstant.ROLE_INITIATOR, user.getRoleCodes());
        variableData.put(FlowCustomVariableConstant.POSITION_INITIATOR, user.getPost());
        variableData.put(FlowCustomVariableConstant.ORG_INITIATOR, user.getOrgId());

        // 内置的系统变量
        return variableData;
    }


    /**
     * 根据流程标识获取流程变量
     * @param busData
     * @param modelKey
     * @return
     */
    public Map<String, Object> getVariablesByModelKey(Map<String, Object> busData, String modelKey) {
        Map<String, Object> variableData = new HashMap<>(16);

        // 流程key
        String processDefinitionKey = modelKey;
        setVariableData(busData, variableData, processDefinitionKey);

        // 流程发起人
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String startUserId = loginUser.getUsername();

        // 发起人角色， 部门， 岗位
        LoginUser user = sysBaseApi.getUserByName(startUserId);
        variableData.put(FlowCustomVariableConstant.ROLE_INITIATOR, user.getRoleCodes());
        variableData.put(FlowCustomVariableConstant.POSITION_INITIATOR, user.getPost());
        variableData.put(FlowCustomVariableConstant.ORG_INITIATOR, user.getOrgId());


        // 内置的系统变量
        return variableData;
    }

    /**
     * 构建流程变量
     * @param busData
     * @param variableData
     * @param processDefinitionKey
     */
    private void setVariableData(Map<String, Object> busData, Map<String, Object> variableData, String processDefinitionKey) {
        // 流程模板信息
        LambdaQueryWrapper<ActCustomModelInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomModelInfo::getModelKey, processDefinitionKey).last("limit 1");
        ActCustomModelInfo one = modelInfoService.getOne(queryWrapper);

        // 非系统变量
        List<ActCustomVariable> list = variableService.list(new LambdaQueryWrapper<ActCustomVariable>().eq(ActCustomVariable::getModelId, one.getModelId())
                .eq(ActCustomVariable::getVariableType, 1).eq(ActCustomVariable::getType, 0));
        if (Objects.nonNull(busData) && MapUtil.isNotEmpty(busData) && CollUtil.isNotEmpty(list)) {
            Object approvalType = busData.get("__APPROVAL_TYPE");
            if (Objects.nonNull(approvalType)) {
                variableData.put("operationType", approvalType);
            }
            list.stream().forEach(variable -> {
                String variableName = variable.getVariableName();
                variableData.put(variableName, busData.get(variableName));
            });
        }
    }
}
