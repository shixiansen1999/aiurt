package com.aiurt.modules.forecast.dto;

import com.aliyun.oss.model.SetBucketQosInfoRequest;
import lombok.Data;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.task.api.history.HistoricTaskInstance;

import org.flowable.bpmn.model.Process;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Data
public class HistoricTaskInfo {



    /**
     * 历史
     */
    private List<HistoricTaskInstance> list = new ArrayList<>();

    /**
     * 允许
     */
    private Boolean addFlag = true;

    /**
     * 节点出现的次数
     */
    private int nodeTime;

    /**
     * 任务是否已结束
     */
    private Boolean isActive = false;

    private Boolean isFeature = false;

    /**
     * 下一个节点的数据
     */
    private Set<String> nextNodeSet = new HashSet<>();

    /**
     * 名称
     */
    private String name;



    private List<String> userNameList;

    /**
     *
     */
    private List<String> realNameList;

    /**
     * 节点定义id
     */
    private String taskDefinitionKey;


    public void addTaskInstance(HistoricTaskInstance historicTaskInstance) {
        this.list.add(historicTaskInstance);
    }

    public void addNextNodeList(String nodeId, int time) {
        if (time !=0) {
            nodeId =  nodeId + "_" + time;
        }
        this.nextNodeSet.add(nodeId);
    }


    public static SequenceFlow createSequenceFlow(String id, String name, String resourceFlowElementId, String targetFlowElementId, String s) {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(id);
        sequenceFlow.setName(name);
        sequenceFlow.setSourceRef(resourceFlowElementId);
        sequenceFlow.setTargetRef(targetFlowElementId);
        return sequenceFlow;
    }



    /**
     * 创建结束节点信息
     * @param id
     * @param name
     * @return
     */
    public static FlowElement createEndFlowElement(String id,String name){
        EndEvent endEvent=new EndEvent();
        endEvent.setId(id);
        endEvent.setName(name);
        return endEvent;
    }

    /**
     * 创建普通任务节点信息
     * @param id
     * @param name
     * @param assignee
     * @return
     */
    public static FlowElement createCommonUserTask(String id, String name, String assignee){
        UserTask userTask=new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        userTask.setAssignee(assignee);
        return userTask;
    }

    /**
     * 创建会签节点信息
     * @param id
     * @param name
     * @return
     */
    public static FlowElement createMultiUserTask(String id,String name){
        UserTask userTask=new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        //分配用户
        userTask.setAssignee("${assignee}");
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics=new MultiInstanceLoopCharacteristics();
//        multiInstanceLoopCharacteristics.setCollectionString("${collectionList}");
        //完成条件,默认所有人都完成
        multiInstanceLoopCharacteristics.setCompletionCondition("${completionCondition}");
        //元素变量多实例,一般和设置的assignee变量是对应的
        multiInstanceLoopCharacteristics.setElementVariable("assignee");
        //集合多实例,用于接收集合数据的表达式
        multiInstanceLoopCharacteristics.setInputDataItem("${itemList}");
        userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
        return userTask;


    }
}
