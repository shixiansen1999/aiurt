package com.aiurt.modules.flow.service;

import com.aiurt.modules.flow.dto.*;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程引擎API的接口封装服务。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowApiService {


    /**
     * 启动流程
     *
     * @param startBpmnDTO
     * @return
     */
    public ProcessInstance start(StartBpmnDTO startBpmnDTO);

    /**
     * 启动流程实例，如果当前登录用户为第一个用户任务的指派者，或者Assginee为流程启动人变量时，
     * 则自动完成第一个用户任务。
     *
     * @param startBpmnDTO 流程定义Id。
     * @return 新启动的流程实例。
     */
    ProcessInstance startAndTakeFirst(StartBpmnDTO startBpmnDTO);

    /**
     * 完成任务，同时提交审批数据。
     *
     * @param task    工作流任务对象。
     * @param comment 审批对象。
     * @param busData 流程任务的变量数据。
     */
    void completeTask(Task task, ActCustomTaskComment comment, Map<String, Object> busData);

    /**
     * 判断当前登录用户是否为流程实例中的用户任务的指派人。或是候选人之一。
     *
     * @param task 流程实例中的用户任务。
     * @return 是返回true，否则false。
     */
    boolean isAssigneeOrCandidate(TaskInfo task);

    /**
     * 获取指定的流程实例对象。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例对象。
     */
    ProcessInstance getProcessInstance(String processInstanceId);

    /**
     * 待办任务
     *
     * @return
     */
    IPage<FlowTaskDTO> listRuntimeTask(Integer pageNo, Integer pageSize, FlowTaskReqDTO flowTaskReqDTO);

    /**
     * 获取流程实例的变量。
     *
     * @param processInstanceId 流程实例Id。
     * @param variableName      变量名。
     * @return 变量值。
     */
    Object getProcessInstanceVariable(String processInstanceId, String variableName);

    /**
     * 获取流程实例的列表。
     *
     * @param processInstanceIdSet 流程实例Id集合。
     * @return 流程实例列表。
     */
    List<ProcessInstance> getProcessInstanceList(Set<String> processInstanceIdSet);

    /**
     * 获取流程定义的列表。
     *
     * @param processDefinitionIdSet 流程定义Id集合。
     * @return 流程定义列表。
     */
    List<ProcessDefinition> getProcessDefinitionList(Set<String> processDefinitionIdSet);

    /**
     * 获取指定流程定义的流程图
     * @param processDefinitionId 流程定义Id
     * @return
     */
    BpmnModel getBpmnModelByDefinitionId(String processDefinitionId);
    /**
     * 获取流程实例的历史流程实例。
     *
     * @param processInstanceId 流程实例Id。
     * @return 历史流程实例。
     */
    HistoricProcessInstance getHistoricProcessInstance(String processInstanceId);
    /**
     * 获取流程图高亮数据。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程图高亮数据。
     */
    JSONObject viewHighlightFlowData(String processInstanceId);

    /**
     * 获取流程实例的已完成历史任务列表。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例已完成的历史任务列表。
     */
    List<HistoricActivityInstance> getHistoricActivityInstanceList(String processInstanceId);

    /**
     * 获取流程实例的待完成任务列表。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例待完成的任务列表。
     */
    List<HistoricActivityInstance> getHistoricUnfinishedInstanceList(String processInstanceId);
    /**
     * 获取指定流程实例和任务Id的当前活动任务。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            流程任务Id。
     * @return 当前流程实例的活动任务。
     */
    Task getProcessInstanceActiveTask(String processInstanceId, String taskId);
    /**
     * 获取流程运行时指定任务的信息。
     *
     * @param processDefinitionId 流程引擎的定义Id。
     * @param processInstanceId   流程引擎的实例Id。
     * @param taskId              流程引擎的任务Id。
     * @return 任务节点的自定义对象数据。
     */
    TaskInfoDTO viewRuntimeTaskInfo(String processDefinitionId, String processInstanceId, String taskId);

    /**
     * 已办任务
     * @param processDefinitionName
     * @param beginDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<FlowHisTaskDTO> listHistoricTask(String processDefinitionName, String beginDate, String endDate, Integer pageNo, Integer pageSize);
}
