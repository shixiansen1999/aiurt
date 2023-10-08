package com.aiurt.modules.forecast.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.cmd.ConditionExpressionV2Cmd;
import com.aiurt.modules.flow.dto.HighLightedNodeDTO;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.forecast.dto.FlowElementPojo;
import com.aiurt.modules.forecast.dto.HistoricTaskInfo;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Service
public class FlowForecastServiceImpl {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private FlowElementUtil flowElementUtil;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ManagementService managementService;

    public HighLightedNodeDTO test(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 历史记录,包括正在运行的节点
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
        list = list.stream().filter(historicTaskInstance -> !StrUtil.equalsIgnoreCase("MI_END", historicTaskInstance.getDeleteReason())).collect(Collectors.toList());
        // 找出数据
        String definitionId = historicProcessInstance.getProcessDefinitionId();
        // bpmnmodel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        // 开始节点
        StartEvent startEvent = bpmnModel.getMainProcess().findFlowElementsOfType(StartEvent.class, false).get(0);

        Map<String, List<String>> userTaskModelMap = new HashMap<>(16);
        // 构建好每个节点的出线图
        buildNextNodeRelation("", startEvent, userTaskModelMap);
        // 出现的次数
        Map<String, Integer> res = new HashMap<>(16);
        // 是否为第一次出现
        Map<String, Boolean> resFlag = new HashMap<>(16);
        LinkedHashMap<String, HistoricTaskInfo> resultMap = new LinkedHashMap<>(16);
        list.stream().forEach(historicTaskInstance -> {
            String taskDefinitionKey = historicTaskInstance.getTaskDefinitionKey();
            Integer time = res.getOrDefault(taskDefinitionKey, 0);

            //
            List<String> nextNodeIdList = userTaskModelMap.get(taskDefinitionKey);
            // 已存在下一步节点
            boolean flag = nextNodeIdList.stream()
                    .anyMatch(noNodeId -> resultMap.containsKey(noNodeId));
            if (flag) {
                time = time +1;
                res.put(taskDefinitionKey, time);

                // 设置之前的节点不能添加了
                Boolean orDefault = resFlag.getOrDefault(taskDefinitionKey + "_" + time, true);
                if (orDefault) {
                    resFlag.put(taskDefinitionKey + "_" + time , false);
                    // 历史数据不允许合并
                    resultMap.forEach((nodeId, historicTaskInfo)->{
                        historicTaskInfo.setAddFlag(false);
                    });
                }
            } else {
                // 被回退的, 但是没有存在下一个节点的数据
                String key = (time == 0 ) ? taskDefinitionKey : taskDefinitionKey+ "_" + (time);
                HistoricTaskInfo lastTaskInfo = resultMap.get(key);
                // 历史节点不允许添加了
                if (Objects.nonNull(lastTaskInfo) && (!lastTaskInfo.getAddFlag())) {
                    // time +1；
                    time = time + 1;
                    res.put(taskDefinitionKey, time);
                }
            }

            // 没有
            if (time == 0) {
                HistoricTaskInfo historicTaskInfo = resultMap.get(taskDefinitionKey);
                if (Objects.isNull(historicTaskInfo)) {
                    historicTaskInfo = new HistoricTaskInfo();
                    resultMap.put(taskDefinitionKey, historicTaskInfo);
                }
                if (historicTaskInfo.getAddFlag()) {
                    historicTaskInfo.addTaskInstance(historicTaskInstance);
                }
                historicTaskInfo.setNodeTime(time);
                historicTaskInfo.setName(historicTaskInstance.getName());

            } else {

                HistoricTaskInfo historicTaskInfo = resultMap.get(taskDefinitionKey + "_" + time);
                if (Objects.isNull(historicTaskInfo)) {
                    historicTaskInfo = new HistoricTaskInfo();
                    resultMap.put(taskDefinitionKey + "_" + time, historicTaskInfo);
                }
                if (historicTaskInfo.getAddFlag()) {
                    historicTaskInfo.addTaskInstance(historicTaskInstance);
                }
                // 设置下一个节点的数据
                historicTaskInfo.setNodeTime(time);
                historicTaskInfo.setName(historicTaskInstance.getName());
            }
        });

        // 处理流程节点

        List<String> runList = new ArrayList<>();
        Map<String, Integer> nodeTimeMap = new HashMap<>();
        resultMap.keySet().stream().forEach(nodeId->{
            HistoricTaskInfo historicTaskInfo = resultMap.get(nodeId);
            List<HistoricTaskInstance> historicTaskInstanceList = historicTaskInfo.getList();
            String taskDefinitionKey = historicTaskInstanceList.get(0).getTaskDefinitionKey();

            nodeTimeMap.put(taskDefinitionKey,  nodeTimeMap.getOrDefault(taskDefinitionKey,0)+1);

            // 调整
            Set<String> deleteReasonSet = historicTaskInstanceList.stream().map(HistoricTaskInstance::getDeleteReason).collect(Collectors.toSet());
            deleteReasonSet.removeAll(Collections.singleton(null));
            // 没有完成数据
            List<HistoricTaskInstance> taskInstanceList = historicTaskInstanceList.stream()
                    .filter(historicTaskInstance -> Objects.isNull(historicTaskInstance.getEndTime())).collect(Collectors.toList());

            // 修改下一个节点数据
            if (CollUtil.isNotEmpty(deleteReasonSet)) {
                // Change parent activity to Activity_0dc8qph
                Set<String> targetSet = deleteReasonSet.stream().map(deleteReason -> StrUtil.replace(deleteReason, "Change parent activity to ", "")).collect(Collectors.toSet());
                targetSet.stream().forEach(id->{
                    historicTaskInfo.addNextNodeList(id, nodeTimeMap.getOrDefault(id, 0));
                });
            } else {
                // 判断, 是否要加一
                int nodeTime = historicTaskInfo.getNodeTime();
                List<String> nextNodeIdList = userTaskModelMap.get(taskDefinitionKey);
                if (CollUtil.isNotEmpty(nextNodeIdList)) {
                    nextNodeIdList.stream().forEach(id->{
                        historicTaskInfo.addNextNodeList(id, nodeTimeMap.getOrDefault(id, 0));
                    });
                }
            }


            if (CollUtil.isNotEmpty(taskInstanceList)) {
                runList.add(taskDefinitionKey);
                historicTaskInfo.setIsActive(true);
            }

        });

        Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
        // 预测未来的节点
        LinkedHashMap<String, HistoricTaskInfo> featureMap = new LinkedHashMap<>(16);
        if (Objects.isNull(historicProcessInstance.getEndTime()) && CollUtil.isNotEmpty(runList)) {
            for (String s : runList) {
                // 节点信息
                FlowElement flowElement = flowElementUtil.getFlowElement(definitionId, s);
                getTargetFlowElement(flowElement, featureMap, variables);
            }

            // 办理人设置, 重新办理的情况
            featureMap.keySet().stream().forEach(nodeId->{
                HistoricTaskInfo historicTaskInfo = featureMap.get(nodeId);
                //
                List<String> nextNodeIdList = userTaskModelMap.get(nodeId);
                nextNodeIdList.stream().forEach(id->{
                    historicTaskInfo.addNextNodeList(id, nodeTimeMap.getOrDefault(id, 0));
                });
                Integer time = nodeTimeMap.getOrDefault(nodeId, 0);
                String key = time == 0 ? nodeId : nodeId+"_"+time;
                resultMap.put(key, historicTaskInfo);
            });
        }

        // 补充信息，办理人

        BpmnModel bpmnModel1 = new BpmnModel();
        // 设置流程信息
        // 此信息都可以通过前期自定义数据,使用时再查询
        Process process = new Process();
        process.setId(historicProcessInstance.getProcessDefinitionKey());
        process.setName(historicProcessInstance.getProcessDefinitionName());
        // 构建用户任务
        List<FlowElement> userTaskList = resultMap.keySet().stream().map(nodeId -> {
            HistoricTaskInfo historicTaskInfo = resultMap.get(nodeId);
            return HistoricTaskInfo.createCommonUserTask(nodeId, historicTaskInfo.getName(), null);
        }).collect(Collectors.toList());
        List<FlowElement> elementList = new ArrayList<>();

        elementList.addAll(userTaskList);

        //把节点放入process
        elementList.stream().forEach(item -> process.addFlowElement(item));

        // 查询各个节点的关系信息,并添加进流程
        List<FlowElementPojo> flowElementPojoList = new ArrayList<>();
        AtomicReference<Integer> t = new AtomicReference<>(0);
        // 构造流转线
        resultMap.keySet().stream().forEach(nodeId->{
            HistoricTaskInfo historicTaskInfo = resultMap.get(nodeId);
            historicTaskInfo.getNextNodeSet().forEach(nextNodeId->{
                FlowElementPojo flowElementPojo = new FlowElementPojo();
                flowElementPojo.setId("sequence_"+ t.get());
                flowElementPojo.setTargetFlowElementId(nextNodeId);
                flowElementPojo.setResourceFlowElementId(nodeId);
                flowElementPojo.setFlowElementType("sequence");
                t.set(t.get() + 1);
                flowElementPojoList.add(flowElementPojo);
            });
        });
        for (FlowElementPojo flowElementPojo:flowElementPojoList){
            SequenceFlow sequenceFlow= HistoricTaskInfo.createSequeneFlow(flowElementPojo.getId(),"",flowElementPojo.getResourceFlowElementId(),
                    flowElementPojo.getTargetFlowElementId(),"");
            process.addFlowElement(sequenceFlow);
        }
        bpmnModel1.addProcess(process);

        // 生成自动布局
        new BpmnAutoLayout(bpmnModel1).execute();

        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] xmlBytes = bpmnXMLConverter.convertToXML(bpmnModel1);
        System.out.println( new String(xmlBytes));
        return null;
    }

    /**
     * 构造节点的与下一节点关系
     * @param userNodeId
     * @param sourceFlowElement
     * @param userTaskModelMap
     */
    public void buildNextNodeRelation(String userNodeId, FlowElement sourceFlowElement, Map<String, List<String>> userTaskModelMap) {
        if (sourceFlowElement instanceof FlowNode) {
            //当前节点必须是FlowNode才做处理，比如UserTask或者GateWay
            FlowNode thisFlowNode = (FlowNode) sourceFlowElement;
            if (sourceFlowElement instanceof UserTask) {
                //
                String id = sourceFlowElement.getId();
                List<String> list = userTaskModelMap.get(id);
                if (Objects.isNull(list)) {
                    list = new ArrayList<>();
                }
                userTaskModelMap.put(id, list);
                if (StrUtil.isNotBlank(userNodeId)) {
                    List<String> userNodeList = userTaskModelMap.get(userNodeId);
                    if (Objects.isNull(userNodeList)) {
                        userNodeList = new ArrayList<>();
                    }
                    userNodeList.add(id);
                    userTaskModelMap.put(userNodeId, userNodeList);
                }
                List<SequenceFlow> targetOutgoings = ((UserTask) sourceFlowElement).getOutgoingFlows();
                for (SequenceFlow targetOutgoing : targetOutgoings) {
                    buildNextNodeRelation(id, targetOutgoing.getTargetFlowElement(), userTaskModelMap);
                }

            }  else {
                List<SequenceFlow> flowNodeOutgoingFlows = thisFlowNode.getOutgoingFlows();
                for (SequenceFlow flowNodeOutgoingFlow : flowNodeOutgoingFlows) {
                    buildNextNodeRelation(userNodeId, flowNodeOutgoingFlow.getTargetFlowElement(), userTaskModelMap);
                }
            }
        }
    }


    /**
     * 获取下一个代办的节点, 没有流程实例
     * @param sourceFlowElement 源节点
     * @param  resultMap 结果集
     * @return
     */
    public void getTargetFlowElement(FlowElement sourceFlowElement, LinkedHashMap<String, HistoricTaskInfo> resultMap, Map<String, Object> variables) {
        //遇到下一个节点是UserTask就返回
        if (sourceFlowElement instanceof FlowNode) {
            //当前节点必须是FlowNode才做处理，比如UserTask或者GateWay
            FlowNode thisFlowNode = (FlowNode) sourceFlowElement;

            if (thisFlowNode.getOutgoingFlows().size() == 1) {
                //如果只有一条连接线，直接找这条连接线的出口节点，然后继续递归获得接下来的节点
                SequenceFlow sequenceFlow = thisFlowNode.getOutgoingFlows().get(0);
                FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                boolean result = true;
                if (StrUtil.isNotBlank(sequenceFlow.getConditionExpression())) {
                    //计算连接线上的表达式
                    result = managementService.executeCommand(new ConditionExpressionV2Cmd(sequenceFlow.getConditionExpression(), variables));
                }
                if (result) {
                    if (targetFlowElement instanceof UserTask) {
                        HistoricTaskInfo historicTaskInfo = new HistoricTaskInfo();
                        historicTaskInfo.setName(targetFlowElement.getName());
                        historicTaskInfo.setIsActive(true);

                        resultMap.put(targetFlowElement.getId(), historicTaskInfo);
                        getTargetFlowElement(targetFlowElement, resultMap, variables);
                    }  else {
                        getTargetFlowElement(targetFlowElement, resultMap, variables);
                    }
                }
            } else if (thisFlowNode.getOutgoingFlows().size() > 1) {
                //如果有多条连接线，遍历连接线，找出一个连接线条件执行为True的，获得它的出口节点
                for (SequenceFlow sequenceFlow : thisFlowNode.getOutgoingFlows()) {
                    boolean result = true;
                    if (!(thisFlowNode instanceof ParallelGateway) && StrUtil.isNotBlank(sequenceFlow.getConditionExpression())) {
                        //计算连接线上的表达式
                        result = managementService.executeCommand(new ConditionExpressionV2Cmd(sequenceFlow.getConditionExpression(), variables));
                    }
                    if (result) {
                        FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                        if (targetFlowElement instanceof UserTask) {
                            HistoricTaskInfo historicTaskInfo = new HistoricTaskInfo();
                            historicTaskInfo.setName(targetFlowElement.getName());
                            historicTaskInfo.setIsActive(true);
                            resultMap.put(targetFlowElement.getId(), historicTaskInfo);
                            getTargetFlowElement(targetFlowElement, resultMap, variables);
                        } else {
                            getTargetFlowElement(targetFlowElement, resultMap, variables);
                        }
                    }
                }
            }
        }
    }
}
