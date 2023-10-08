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




    public static void main(String[] args) {
        BpmnModel bpmnModel=new BpmnModel();
        //设置流程信息
        //此信息都可以通过前期自定义数据,使用时再查询
        Process process= new Process();
        process.setId("test_model_3");
        process.setName("测试流程图三");
        //添加流程节点信息---start
        String startId="startEvent_id_1";
        String startName="开始_1";
        String endId="endEvent_id_1";
        String endName="结束_1";
        //创建数组存储所有流程节点信息
        List<FlowElement> elementList=new ArrayList<>();
        //创建开始节点
        FlowElement startFlowElement=createStartFlowElement(startId,startName);
        FlowElement endFlowElement=createEndFlowElement(endId,endName);
        elementList.add(startFlowElement);
        elementList.add(endFlowElement);

        //查询普通任务节点信息
        elementList.addAll(findUserTaskElements());

        //把节点放入process
        elementList.stream().forEach(item -> process.addFlowElement(item));

        //查询各个节点的关系信息,并添加进流程
        List<FlowElementPojo> flowElementPojoList =createCirculationSequence();
        for (FlowElementPojo flowElementPojo:flowElementPojoList){
            SequenceFlow sequenceFlow= createSequeneFlow(flowElementPojo.getId(),"流转",flowElementPojo.getResourceFlowElementId(),
                    flowElementPojo.getTargetFlowElementId(),"${a==\"f\"}");
            process.addFlowElement(sequenceFlow);
        }

        bpmnModel.addProcess(process);

        // 生成自动布局
        new BpmnAutoLayout(bpmnModel).execute();

        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] xmlBytes = bpmnXMLConverter.convertToXML(bpmnModel);
        System.out.println( new String(xmlBytes));
    }

    public static SequenceFlow createSequeneFlow(String id, String name, String resourceFlowElementId, String targetFlowElementId, String s) {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(id);
        sequenceFlow.setName(name);
        sequenceFlow.setSourceRef(resourceFlowElementId);
        sequenceFlow.setTargetRef(targetFlowElementId);
        return sequenceFlow;
    }

    private static Collection<? extends FlowElement> findUserTaskElements() {
        List<FlowElement> userList = new ArrayList<>();
        userList.add(createCommonUserTask("userTask_0","userTask_0", "admin"));
        userList.add(createCommonUserTask("userTask_1","userTask_1", "admin"));
        userList.add(createCommonUserTask("userTask_2","userTask_2", "admin"));
        return userList;
    }


    /**
     * 创建开始节点信息
     * @return
     */
    public static FlowElement createStartFlowElement(String id,String name){
        StartEvent startEvent=new StartEvent();
        startEvent.setId(id);
        startEvent.setName(name);
        return startEvent;
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

    /**
     * 查询各节点关联流转信息,即流转线
     *FlowElementPojo 是自定义类
     */
    public static List<FlowElementPojo> createCirculationSequence(){


        List<FlowElementPojo> list=new ArrayList<>();
        FlowElementPojo flowElementPojo_start=new FlowElementPojo();
        flowElementPojo_start.setId("sequence_id_1");
        flowElementPojo_start.setTargetFlowElementId("userTask_0");
        flowElementPojo_start.setResourceFlowElementId("startEvent_id_1");
        flowElementPojo_start.setFlowElementType("sequence");

        FlowElementPojo flowElementPojo_user_0=new FlowElementPojo();
        flowElementPojo_user_0.setId("sequence_id_2");
        flowElementPojo_user_0.setTargetFlowElementId("userTask_1");
        flowElementPojo_user_0.setResourceFlowElementId("userTask_0");
        flowElementPojo_user_0.setFlowElementType("sequence");

        FlowElementPojo flowElementPojo_user_1=new FlowElementPojo();
        flowElementPojo_user_1.setId("sequence_id_3");
        flowElementPojo_user_1.setTargetFlowElementId("userTask_2");
        flowElementPojo_user_1.setResourceFlowElementId("userTask_1");
        flowElementPojo_user_1.setFlowElementType("sequence");

        FlowElementPojo flowElementPojo_user_2=new FlowElementPojo();
        flowElementPojo_user_2.setId("sequence_id_4");
        flowElementPojo_user_2.setTargetFlowElementId("endEvent_id_1");
        flowElementPojo_user_2.setResourceFlowElementId("userTask_2");
        flowElementPojo_user_2.setFlowElementType("sequence");

        list.add(flowElementPojo_start);
        list.add(flowElementPojo_user_0);
        list.add(flowElementPojo_user_1);
        list.add(flowElementPojo_user_2);

        return list;

    }
}
