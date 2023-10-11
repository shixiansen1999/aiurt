package com.aiurt.modules.forecast.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.cmd.ConditionExpressionV2Cmd;
import com.aiurt.modules.flow.dto.HighLightedNodeDTO;
import com.aiurt.modules.flow.dto.HighLightedUserInfoDTO;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.forecast.dto.FlowElementDTO;
import com.aiurt.modules.forecast.dto.HistoricTaskInfo;
import com.aiurt.modules.forecast.service.IFlowForecastService;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.service.DefaultSelectUserService;
import com.aiurt.modules.user.service.IActCustomUserService;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Service
public class FlowForecastServiceImpl implements IFlowForecastService {

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

    @Autowired
    private IActCustomUserService customUserService;

    @Autowired
    private DefaultSelectUserService defaultSelectUserService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * 流程预测
     * @param processInstanceId
     * @return
     */
    @Override
    public HighLightedNodeDTO flowChart(String processInstanceId) {
        HighLightedNodeDTO highLightedNodeDTO = new HighLightedNodeDTO();
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

        // 合并任务
        LinkedHashMap<String, HistoricTaskInfo> resultMap = mergeTask(list, userTaskModelMap);

        // 正在运行的任务，处理流程节点，构建下一个节点数据
        List<String> runList = new ArrayList<>();
        // 每个节点的出现的次数
        Map<String, Integer> nodeTimeMap = new HashMap<>();
        // 处理历史任务，以及查找每个节点的出现的次数，正在运行的任务
        processHistoricTask(userTaskModelMap, resultMap, runList, nodeTimeMap);



        // 预测未来的节点
        buildFeatureTask(processInstanceId, historicProcessInstance, definitionId, userTaskModelMap, resultMap, runList, nodeTimeMap);


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
        List<FlowElementDTO> flowElementPojoList = new ArrayList<>();


        Set<String> collect = resultMap.values().stream().map(HistoricTaskInfo::getUserNameList).flatMap(List::stream).collect(Collectors.toSet());
        List<LoginUser> loginUserList = sysBaseAPI.getLoginUserList(new ArrayList<>(collect));
        Map<String, String> userMap = loginUserList.stream().collect(Collectors.toMap(LoginUser::getUsername, LoginUser::getRealname, (t1, t2) -> t1));

        // 构建已完成
        Set<String> finishedTaskSet = new HashSet<>();
        Set<String> finishedSequenceFlowSet = new HashSet<>();
        Set<String> unfinishedTaskSet = new HashSet<>();
        Set<String> featureTaskSet = new HashSet<>();
        Set<String> featureSequenceFlowSet = new HashSet<>();
        List<HighLightedUserInfoDTO> highLightedUserInfoDTOList = new ArrayList<>();
        setSightedNodeInfo(resultMap, flowElementPojoList, userMap, finishedTaskSet, finishedSequenceFlowSet, unfinishedTaskSet, featureTaskSet, featureSequenceFlowSet, highLightedUserInfoDTOList);

        for (FlowElementDTO flowElementPojo:flowElementPojoList){
            SequenceFlow sequenceFlow= HistoricTaskInfo.createSequenceFlow(flowElementPojo.getId(),"",flowElementPojo.getResourceFlowElementId(),
                    flowElementPojo.getTargetFlowElementId(),"");
            process.addFlowElement(sequenceFlow);
        }
        bpmnModel1.addProcess(process);

        // 生成自动布局
        new BpmnAutoLayout(bpmnModel1).execute();

        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] xmlBytes = bpmnXMLConverter.convertToXML(bpmnModel1);
        highLightedNodeDTO.setModelXml( new String(xmlBytes));
        highLightedNodeDTO.setFinishedTaskSet(finishedTaskSet);
        highLightedNodeDTO.setFinishedSequenceFlowSet(finishedSequenceFlowSet);
        highLightedNodeDTO.setFeatureTaskSet(featureTaskSet);
        highLightedNodeDTO.setFeatureSequenceFlowSet(featureSequenceFlowSet);
        highLightedNodeDTO.setUnfinishedTaskSet(unfinishedTaskSet);
        highLightedNodeDTO.setHighLightedUserInfoDTOs(highLightedUserInfoDTOList);
        return highLightedNodeDTO;
    }

    /**
     *
     * @param resultMap
     * @param flowElementPojoList
     * @param userMap
     * @param finishedTaskSet
     * @param finishedSequenceFlowSet
     * @param unfinishedTaskSet
     * @param featureTaskSet
     * @param featureSequenceFlowSet
     * @param highLightedUserInfoDTOList
     */
    private void setSightedNodeInfo(LinkedHashMap<String, HistoricTaskInfo> resultMap, List<FlowElementDTO> flowElementPojoList, Map<String, String> userMap, Set<String> finishedTaskSet, Set<String> finishedSequenceFlowSet, Set<String> unfinishedTaskSet, Set<String> featureTaskSet, Set<String> featureSequenceFlowSet, List<HighLightedUserInfoDTO> highLightedUserInfoDTOList) {
        AtomicReference<Integer> t = new AtomicReference<>(0);
        resultMap.keySet().stream().forEach(nodeId->{
            HistoricTaskInfo historicTaskInfo = resultMap.get(nodeId);

            List<String> userNameList = historicTaskInfo.getUserNameList();
            List<String> realNameList = userNameList.stream().map(userName -> userMap.get(userName)).collect(Collectors.toList());
            historicTaskInfo.setRealNameList(realNameList);
            HighLightedUserInfoDTO highLightedUserInfoDTO = new HighLightedUserInfoDTO();
            highLightedUserInfoDTO.setNodeId(nodeId);
            highLightedUserInfoDTO.setRealName(StrUtil.join(";", realNameList));
            highLightedUserInfoDTOList.add(highLightedUserInfoDTO);
            // 运行，
            if (historicTaskInfo.getIsActive()) {
                unfinishedTaskSet.add(nodeId);
            }else {
                //  可能运行
                if (historicTaskInfo.getIsFeature()) {
                    featureTaskSet.add(nodeId);
                }else {
                    finishedTaskSet.add(nodeId);
                }
            }

            //
            historicTaskInfo.getNextNodeSet().forEach(nextNodeId->{
                String flowId = "sequence_" + t.get();
                FlowElementDTO flowElementPojo = new FlowElementDTO();
                flowElementPojo.setId(flowId);
                flowElementPojo.setTargetFlowElementId(nextNodeId);
                flowElementPojo.setResourceFlowElementId(nodeId);
                flowElementPojo.setFlowElementType("sequence");
                t.set(t.get() + 1);
                HistoricTaskInfo taskInfo = resultMap.get(nextNodeId);
                if (Objects.nonNull(taskInfo)) {
                    if (taskInfo.getIsFeature()) {
                        featureSequenceFlowSet.add(flowId);
                    }else {
                        finishedSequenceFlowSet.add(flowId);
                    }
                }
                flowElementPojoList.add(flowElementPojo);
            });
        });
    }

    /**
     * 预测未来的节点
     * @param processInstanceId
     * @param historicProcessInstance
     * @param definitionId
     * @param userTaskModelMap
     * @param resultMap
     * @param runList
     * @param nodeTimeMap
     */
    private void buildFeatureTask(String processInstanceId, HistoricProcessInstance historicProcessInstance, String definitionId, Map<String, List<String>> userTaskModelMap, LinkedHashMap<String, HistoricTaskInfo> resultMap, List<String> runList, Map<String, Integer> nodeTimeMap) {
        if (Objects.isNull(historicProcessInstance.getEndTime()) && CollUtil.isNotEmpty(runList)) {
            Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
            LinkedHashMap<String, HistoricTaskInfo> featureMap = new LinkedHashMap<>(16);
            for (String s : runList) {
                // 节点信息
                FlowElement flowElement = flowElementUtil.getFlowElement(definitionId, s);
                getTargetFlowElement(flowElement, featureMap, variables);
            }
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            // 办理人设置, 重新办理的情况
            featureMap.keySet().stream().forEach(nodeId->{
                HistoricTaskInfo historicTaskInfo = featureMap.get(nodeId);

                List<String> nextNodeIdList = userTaskModelMap.get(nodeId);
                nextNodeIdList.stream().forEach(id->{
                    historicTaskInfo.addNextNodeList(id, nodeTimeMap.getOrDefault(id, 0));
                });
                // 回退的节点需要重新测试次数
                Integer time = nodeTimeMap.getOrDefault(nodeId, 0);
                String key = time == 0 ? nodeId : nodeId+"_"+time;

                ActCustomUser actCustomUser = customUserService.getActCustomUserByTaskInfo(processInstance.getProcessDefinitionId(), nodeId, "0");
                List<String> userList = defaultSelectUserService.getAllUserList(actCustomUser, variables, processInstance);
                historicTaskInfo.setUserNameList(userList);
                resultMap.put(key, historicTaskInfo);
            });
        }
    }

    private void processHistoricTask(Map<String, List<String>> userTaskModelMap, LinkedHashMap<String, HistoricTaskInfo> resultMap, List<String> runList, Map<String, Integer> nodeTimeMap) {
        resultMap.keySet().stream().forEach(nodeId->{
            HistoricTaskInfo historicTaskInfo = resultMap.get(nodeId);
            List<HistoricTaskInstance> historicTaskInstanceList = historicTaskInfo.getList();
            List<String> userNameList = historicTaskInstanceList.stream().map(HistoricTaskInstance::getAssignee).collect(Collectors.toList());
            historicTaskInfo.setUserNameList(userNameList);
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
    }

    @NotNull
    private LinkedHashMap<String, HistoricTaskInfo> mergeTask(List<HistoricTaskInstance> list, Map<String, List<String>> userTaskModelMap) {
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

            // 没有回退
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

                // 存在回退的情况
            } else {
                HistoricTaskInfo historicTaskInfo = resultMap.get(taskDefinitionKey + "_" + time);
                if (Objects.isNull(historicTaskInfo)) {
                    historicTaskInfo = new HistoricTaskInfo();
                    resultMap.put(taskDefinitionKey + "_" + time, historicTaskInfo);
                }
                if (historicTaskInfo.getAddFlag()) {
                    historicTaskInfo.addTaskInstance(historicTaskInstance);
                }
                // 设置节点信息
                historicTaskInfo.setNodeTime(time);
                historicTaskInfo.setName(historicTaskInstance.getName());
            }
        });
        return resultMap;
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

            } else {
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
                        historicTaskInfo.setIsActive(false);
                        historicTaskInfo.setIsFeature(true);

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
                            historicTaskInfo.setIsFeature(false);
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
