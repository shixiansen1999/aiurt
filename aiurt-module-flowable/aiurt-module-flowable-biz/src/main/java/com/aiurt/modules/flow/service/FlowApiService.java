package com.aiurt.modules.flow.service;

import com.aiurt.modules.flow.dto.*;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import java.util.Collection;
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
    void startAndTakeFirst(StartBpmnDTO startBpmnDTO);

    /**
     * 完成任务，同时提交审批数据。
     *
     * @param task    工作流任务对象。
     * @param comment 审批对象。
     * @param busData 流程任务的变量数据。
     */
    void completeTask(Task task, ActCustomTaskComment comment, Map<String, Object> busData);

    /**
     * 完成任务
     * @param task
     * @param comment
     * @param busData
     * @param variableData
     */
    void completeTask(Task task, ActCustomTaskComment comment, Map<String, Object> busData, Map<String, Object> variableData);

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
     * @param flowTaskReqDTO 查询条件
     * @param pageNo 分页页码
     * @param pageSize 分页大小
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
     * <<<<<<< Updated upstream
     * 获取指定流程定义的流程图
     *
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
    HighLightedNodeDTO viewHighlightFlowData(String processInstanceId);

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
     *
     * @param processDefinitionName
     * @param beginDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<FlowHisTaskDTO> listHistoricTask(String processDefinitionName, String beginDate, String endDate, Integer pageNo, Integer pageSize);

    /**
     * 给用户节点添加监听器
     *
     * @param userTask
     * @param listenerClazz
     */
    void addTaskCreateListener(UserTask userTask, Class<? extends TaskListener> listenerClazz);

    /**
     * 转办任务
     *
     * @param params
     */
    void turnTask(TurnTaskDTO params);

    /**
     * 获取可驳回节点列表
     *
     * @param processInstanceId
     * @param taskId
     * @return
     */
    List<FlowNodeDTO> getBackNodesByProcessInstanceId(String processInstanceId, String taskId);
    /**
     * 流程实例
     * @param reqDTO
     * @return
     */
    IPage<HistoricProcessInstanceDTO> listAllHistoricProcessInstance(HistoricProcessInstanceReqDTO reqDTO);
    /**
     * 回退到上一个用户任务节点。如果没有指定，则回退到上一个任务。
     *
     * @param task      当前活动任务。
     * @param targetKey 指定回退到的任务标识。如果为null，则回退到上一个任务。
     * @param forReject true表示驳回，false为撤回。
     * @param comment    驳回或者撤销的原因。
     */
    void backToRuntimeTask(Task task, String targetKey, boolean forReject, String comment);
    /**
     * 根据流程定义Id查询流程定义对象。
     *
     * @param processDefinitionId 流程定义Id。
     * @return 流程定义对象。
     */
    ProcessDefinition getProcessDefinitionById(String processDefinitionId);
    /**
     * 获取指定流程定义的全部流程节点。
     *
     * @param processDefinitionId 流程定义Id。
     * @return 当前流程定义的全部节点集合。
     */
    Collection<FlowElement> getProcessAllElements(String processDefinitionId);
    /**
     * 获取流程实例的已完成历史任务列表，同时按照每个活动实例的开始时间升序排序。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例已完成的历史任务列表。
     */
    List<HistoricActivityInstance> getHistoricActivityInstanceListOrderByStartTime(String processInstanceId);


    /**
     * 终止流程
     * @param instanceDTO
     */
    void stopProcessInstance(StopProcessInstanceDTO instanceDTO);

    /**
     * 删除流程
     * @param processInstanceId
     */
    void deleteProcessInstance(String processInstanceId);

    /**
     * 获取开始节点之后的第一个任务节点的数据。
     *
     * @param processDefinitionKey 流程标识。
     * @return 任务节点的自定义对象数据。
     */
    TaskInfoDTO viewInitialTaskInfo(String processDefinitionKey);

    /**
     * 提交任务
     * @param taskCompleteDTO
     */
    void completeTask(TaskCompleteDTO taskCompleteDTO);

    /**
     * 根据业务数据获取历史活动
     * @param businessKey
     * @return
     */
    List<HistoricTaskInfo> getHistoricLog(String businessKey);

    /**
     * 根据ProcessInstanceId 获取历史记录
     * @param processInstanceId
     * @return
     */
    List<HistoricTaskInfo> getHistoricLogByProcessInstanceId(String processInstanceId);

    /**
     * 根据ProcessInstanceId 获取流程实例状态
     * @param processInstanceId 流程实例id
     * @return
     */
    ProcessInstanceStateResult getProcessInstanceState(String processInstanceId);

    /**
     * 任务已结束的流程表单信息
     * @param processInstanceId
     * @return
     */
    TaskInfoDTO viewEndProcessTaskInfo(String processInstanceId);
}
