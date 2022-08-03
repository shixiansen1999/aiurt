package com.aiurt.modules.flow.service;

import com.aiurt.modules.flow.dto.FlowTaskDTO;
import com.aiurt.modules.flow.dto.FlowTaskReqDTO;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
}
