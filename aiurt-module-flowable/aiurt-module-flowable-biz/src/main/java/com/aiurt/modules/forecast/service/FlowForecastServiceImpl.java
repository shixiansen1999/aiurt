package com.aiurt.modules.forecast.service;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.cmd.ConditionExpressionV2Cmd;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.forecast.dto.HistoricTaskInfo;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author fgw
 */
@Service
public class FlowForecastServiceImpl {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private FlowElementUtil flowElementUtil;

    @Autowired
    private RepositoryService repositoryService;

    public void test(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 历史记录
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
        // 找出数据
        //
        String definitionId = historicProcessInstance.getProcessDefinitionId();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        StartEvent startEvent = bpmnModel.getMainProcess().findFlowElementsOfType(StartEvent.class, false).get(0);



        Map<String, List<String>> userTaskModelMap = new HashMap<>();
        buildNextNodeRelation("", startEvent, userTaskModelMap);
        // 出现的字数
        Map<String, Integer> res = new HashMap<>(16);
        Map<String, Boolean> resFlag = new HashMap<>(16);
        Map<String, HistoricTaskInfo> resultMap = new HashMap<>(16);
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
                    resultMap.forEach((nodeId,historicTaskInfo)->{
                        historicTaskInfo.setAddFlag(false);
                    });
                }
            } else {
                // 被回退的, 但是没有存在下一个节点的数据
                String key = (time == 0 || time == 1) ? taskDefinitionKey : taskDefinitionKey+ "_" + (time-1);
                HistoricTaskInfo lastTaskInfo = resultMap.get(key);

                // 历史节点不允许添加了
                if (Objects.nonNull(lastTaskInfo) && (!lastTaskInfo.getAddFlag())) {
                    // time +1；
                    time = time +1;
                    res.put(taskDefinitionKey, time);
                }
            }

            //
            // 没有
            if (time == 0) {
                HistoricTaskInfo historicTaskInfo = new HistoricTaskInfo();
                if (historicTaskInfo.getAddFlag()) {
                    historicTaskInfo.addTaskInstance(historicTaskInstance);
                }
                resultMap.put(taskDefinitionKey, historicTaskInfo);
                historicTaskInfo.setNextNodeList(nextNodeIdList);

            } else {

                HistoricTaskInfo historicTaskInfo = resultMap.get(taskDefinitionKey + "_" + time);
                if (Objects.isNull(historicTaskInfo)) {
                    historicTaskInfo = new HistoricTaskInfo();
                    resultMap.put(taskDefinitionKey + "_" + time, historicTaskInfo);
                }
                if (historicTaskInfo.getAddFlag()) {
                    historicTaskInfo.addTaskInstance(historicTaskInstance);
                }
                historicTaskInfo.setNextNodeList(nextNodeIdList);
            }


            // 已存在resultMap中， 标识回退节点

            /*if (flag) {



                //
                resultMap.put(taskDefinitionKey+"_"+time, new HistoricTaskInfo());
            }


            //

            // 判断是否已经出现了下一节点的数据
            // 没有出现下一个节点的数据，
            // 出现了
            HistoricTaskInfo instanceList = resultMap.get(taskDefinitionKey);
            *//*if (Objects.isNull(instanceList)) {
                instanceList = new ArrayList<>();
            }
            instanceList.add(historicTaskInstance);
            resultMap.put(taskDefinitionKey, instanceList);*/
        });
        System.out.println("123445");

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
}
