package com.aiurt.modules.flow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.flow.dto.FlowTaskDTO;
import com.aiurt.modules.flow.dto.FlowTaskReqDTO;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.aiurt.modules.flow.mapper.ActCustomTaskCommentMapper;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.mapper.ActCustomTaskExtMapper;
import com.aiurt.modules.utils.ReflectionService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Slf4j
@Service
public class FlowApiServiceImpl implements FlowApiService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private FlowElementUtil flowElementUtil;
    @Autowired
    private ReflectionService reflectionService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ActCustomTaskCommentMapper customTaskCommentMapper;
    @Autowired
    private ActCustomTaskExtMapper customTaskExtMapper;

    /**
     * @param startBpmnDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance start(StartBpmnDTO startBpmnDTO) {
        log.info("启动流程请求参数：[{}]", JSON.toJSONString(startBpmnDTO));
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(loginUser)) {
            throw new AiurtBootException("无法启动流程，请重新登录！");
        }

        //todo 判断是否是动态表单

        // 保存中间业务数据
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(FlowConstant.PROC_INSTANCE_INITIATOR_VAR, loginUser.getUsername());
        variableMap.put(FlowConstant.PROC_INSTANCE_START_USER_NAME_VAR, loginUser.getUsername());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(startBpmnDTO.getModelKey());
        // 启动流程
        return processInstance;
    }

    /**
     * 启动流程实例，如果当前登录用户为第一个用户任务的指派者，或者Assginee为流程启动人变量时，
     * 则自动完成第一个用户任务。
     *
     * @param startBpmnDTO 流程定义Id。
     * @return 新启动的流程实例。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startAndTakeFirst(StartBpmnDTO startBpmnDTO) {
        log.info("启动流程请求参数：[{}]", JSON.toJSONString(startBpmnDTO));
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(loginUser)) {
            throw new AiurtBootException("无法启动流程，请重新登录！");
        }
        // 验证流程定义数据的合法性。
        Result<ProcessDefinition> processDefinitionResult = flowElementUtil.verifyAndGetFlowEntry(startBpmnDTO.getModelKey());
        if (!processDefinitionResult.isSuccess()) {
            throw new AiurtBootException(processDefinitionResult.getMessage());
        }

        ProcessDefinition result = processDefinitionResult.getResult();
        if (!result.isSuspended()) {
            throw new AiurtBootException("当前流程定义已被挂起，不能启动新流程！");
        }

        // todo 判断是否是动态表单

        // 设置流程变量
        Map<String, Object> busData = startBpmnDTO.getBusData();
        this.initAndGetProcessInstanceVariables(busData);

        // 根据key查询第一个用户任务
        UserTask userTask = flowElementUtil.getFirstUserTaskByModelKey(startBpmnDTO.getModelKey());
        Task task = BeanUtil.copyProperties(userTask, Task.class);

        // 保存中间业务数据，将业务数据id返回
        String businessKey = saveBusData(result.getId(), task.getId());

        // 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(startBpmnDTO.getModelKey(), businessKey, busData);

        // 完成流程启动后的第一个任务
        if (StrUtil.equalsAny(task.getAssignee(), loginUser.getUsername(), FlowConstant.START_USER_NAME_VAR)) {
            // 按照规则，调用该方法的用户，就是第一个任务的assignee，因此默认会自动执行complete。
            if (ObjectUtil.isNotEmpty(startBpmnDTO.getCustomTaskComment())) {
                startBpmnDTO.getCustomTaskComment().fillWith(task);
            }
            this.completeTask(task, startBpmnDTO.getCustomTaskComment(), startBpmnDTO.getBusData());
        }
        return processInstance;
    }

    /**
     * 设置流程变量
     *
     * @param busData
     */
    private void initAndGetProcessInstanceVariables(Map<String, Object> busData) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(loginUser)) {
            throw new AiurtBootException("无法启动流程，请重新登录！");
        }
        busData.put(FlowConstant.PROC_INSTANCE_INITIATOR_VAR, loginUser.getUsername());
        busData.put(FlowConstant.PROC_INSTANCE_START_USER_NAME_VAR, loginUser.getUsername());
    }


    /**
     * 保存业务数据
     *
     * @param pProcessDefinitionId
     * @param taskId
     * @return
     */
    public String saveBusData(String pProcessDefinitionId, String taskId) {
        List<ActCustomTaskExt> actCustomTaskExts = customTaskExtMapper.selectList(
                new LambdaQueryWrapper<ActCustomTaskExt>()
                        .eq(ActCustomTaskExt::getProcessDefinitionId, pProcessDefinitionId)
                        .eq(ActCustomTaskExt::getTaskId, taskId));

        if (CollUtil.isNotEmpty(actCustomTaskExts)) {
            JSONObject jsonObject = JSONObject.parseObject(actCustomTaskExts.get(0).getFormJson());
            if (ObjectUtil.isNotEmpty(jsonObject)) {
                List<String> className = StrUtil.split((String) jsonObject.get("className"), '.');
                try {
                    if (CollUtil.isNotEmpty(className)) {
                        reflectionService.invokeService(className.get(0), className.get(1), null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }


    /**
     * 启动流程并提交第一个用户节点
     *
     * @param startBpmnDTO 流程定义Id。
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startAndCompleteFirst(StartBpmnDTO startBpmnDTO) {
        log.info("启动流程请求参数：[{}]", JSON.toJSONString(startBpmnDTO));
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(loginUser)) {
            throw new AiurtBootException("无法启动流程，请重新登录！");
        }
        // 判断是否是动态表单

        // 保存中间业务数据

        // 启动流程
        return null;
    }

    /**
     * 完成任务，同时提交审批数据。
     *
     * @param task    工作流任务对象。
     * @param comment 审批对象。
     * @param busData 流程任务的变量数据。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void completeTask(Task task, ActCustomTaskComment comment, Map<String, Object> busData) {
        // 获取流程任务
        Task processInstanceActiveTask = this.getProcessInstanceActiveTask(task.getProcessInstanceId(), task.getId());

        // 验证流程任务的合法性。
        this.verifyAndGetRuntimeTaskInfo(processInstanceActiveTask);

        // 流程业务数据状态更改

        // 判断当前完成执行的任务，是否存在抄送设置

        // 增加流程批注数据
        if (comment != null) {
            comment.fillWith(processInstanceActiveTask);
            comment.setCreateRealname(checkLogin().getRealname());
            customTaskCommentMapper.insert(comment);
        }

        // 完成任务
        taskService.complete(task.getId(), busData);

        // 推送流程消息
    }

    /**
     * 获取指定流程实例和任务Id的当前活动任务。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            流程任务Id。
     * @return 当前流程实例的活动任务。
     */
    public Task getProcessInstanceActiveTask(String processInstanceId, String taskId) {
        TaskQuery query = taskService.createTaskQuery().processInstanceId(processInstanceId);
        if (StrUtil.isNotBlank(taskId)) {
            query.taskId(taskId);
        }
        return query.active().singleResult();
    }

    /**
     * 判断当前登录用户是否为流程实例中的用户任务的指派人。或是候选人之一。
     *
     * @param task 流程实例中的用户任务。
     * @return 是返回true，否则false。
     */
    @Override
    public boolean isAssigneeOrCandidate(TaskInfo task) {
        LoginUser loginUser = checkLogin();
        String username = loginUser.getUsername();

        if (StrUtil.isNotBlank(task.getAssignee())) {
            return StrUtil.equals(username, task.getAssignee());
        }
        TaskQuery query = taskService.createTaskQuery();
        this.buildCandidateCondition(query, loginUser);
        return query.active().count() != 0;
    }

    /**
     * 获取指定的流程实例对象。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例对象。
     */
    @Override
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    /**
     * 待办任务
     *
     * @param pageNo
     * @param pageSize
     * @param flowTaskReqDTO
     * @return
     */
    @Override
    public IPage<FlowTaskDTO> listRuntimeTask(Integer pageNo, Integer pageSize, FlowTaskReqDTO flowTaskReqDTO) {
        Page<FlowTaskDTO> result = new Page<>();
        TaskQuery query = taskService.createTaskQuery().active();
        if (StrUtil.isNotBlank(flowTaskReqDTO.getProcessDefinitionKey())) {
            query.processDefinitionKey(flowTaskReqDTO.getProcessDefinitionKey());
        }
        if (StrUtil.isNotBlank(flowTaskReqDTO.getProcessDefinitionName())) {
            query.processDefinitionNameLike("%" + flowTaskReqDTO.getProcessDefinitionName() + "%");
        }
        if (StrUtil.isNotBlank(flowTaskReqDTO.getTaskName())) {
            query.taskNameLike("%" + flowTaskReqDTO.getTaskName() + "%");
        }
        this.buildCandidateCondition(query, checkLogin());
        long totalCount = query.count();
        query.orderByTaskCreateTime().desc();
        int firstResult = (pageNo - 1) * pageSize;
        List<Task> taskList = query.listPage(firstResult, pageSize);
        result.setTotal(totalCount);
        result.setRecords(convertToFlowTaskList(taskList));
        result.setCurrent(pageNo);
        result.setSize(pageSize);
        return result;
    }

    /**
     * 将流程任务列表数据，转换为前端可以显示的流程对象。
     *
     * @param taskList 流程引擎中的任务列表。
     * @return 前端可以显示的流程任务列表。
     */
    public List<FlowTaskDTO> convertToFlowTaskList(List<Task> taskList) {
        List<FlowTaskDTO> flowTaskVoList = new LinkedList<>();
        if (CollUtil.isEmpty(taskList)) {
            return flowTaskVoList;
        }
        Set<String> processDefinitionIdSet = taskList.stream()
                .map(Task::getProcessDefinitionId).collect(Collectors.toSet());
        Set<String> procInstanceIdSet = taskList.stream()
                .map(Task::getProcessInstanceId).collect(Collectors.toSet());

        List<ProcessInstance> instanceList = this.getProcessInstanceList(procInstanceIdSet);
        Map<String, ProcessInstance> instanceMap =
                instanceList.stream().collect(Collectors.toMap(ProcessInstance::getId, c -> c));
        List<ProcessDefinition> definitionList = this.getProcessDefinitionList(processDefinitionIdSet);
        Map<String, ProcessDefinition> definitionMap =
                definitionList.stream().collect(Collectors.toMap(ProcessDefinition::getId, c -> c));

        for (Task task : taskList) {
            FlowTaskDTO flowTaskVo = new FlowTaskDTO();
            flowTaskVo.setTaskId(task.getId());
            flowTaskVo.setTaskName(task.getName());
            flowTaskVo.setTaskKey(task.getTaskDefinitionKey());
            flowTaskVo.setTaskFormKey(task.getFormKey());
//            flowTaskVo.setEntryId(flowEntryPublishMap.get(task.getProcessDefinitionId()).getEntryId());

            ProcessDefinition processDefinition = definitionMap.get(task.getProcessDefinitionId());
            flowTaskVo.setProcessDefinitionId(processDefinition.getId());
            flowTaskVo.setProcessDefinitionName(processDefinition.getName());
            flowTaskVo.setProcessDefinitionKey(processDefinition.getKey());
            flowTaskVo.setProcessDefinitionVersion(processDefinition.getVersion());
            ProcessInstance processInstance = instanceMap.get(task.getProcessInstanceId());
            flowTaskVo.setProcessInstanceId(processInstance.getId());
            Object initiator = this.getProcessInstanceVariable(
                    processInstance.getId(), FlowConstant.PROC_INSTANCE_INITIATOR_VAR);
            flowTaskVo.setProcessInstanceInitiator(initiator.toString());
            flowTaskVo.setProcessInstanceStartTime(processInstance.getStartTime());
            flowTaskVo.setBusinessKey(processInstance.getBusinessKey());
            flowTaskVoList.add(flowTaskVo);
        }
        return flowTaskVoList;
    }

    /**
     * 获取流程实例的变量。
     *
     * @param processInstanceId 流程实例Id。
     * @param variableName      变量名。
     * @return 变量值。
     */
    @Override
    public Object getProcessInstanceVariable(String processInstanceId, String variableName) {
        return runtimeService.getVariable(processInstanceId, variableName);
    }

    /**
     * 构建任务查询条件
     *
     * @param query
     * @param loginUser
     */
    private void buildCandidateCondition(TaskQuery query, LoginUser loginUser) {
        if (ObjectUtil.isNotEmpty(loginUser)) {

            Set<String> groupIdSet = new HashSet<>();
            // NOTE: 需要注意的是，部门Id、或者其他类型的分组Id，他们之间一定不能重复。
            String orgId = loginUser.getOrgId();
            if (StrUtil.isNotEmpty(orgId)) {
                groupIdSet.add(orgId);
            }

            String roleIds = loginUser.getRoleIds();
            if (StrUtil.isNotEmpty(roleIds)) {
                groupIdSet.addAll(StrUtil.split(roleIds, ','));
            }

            if (CollUtil.isNotEmpty(groupIdSet)) {
                query.or().taskCandidateGroupIn(groupIdSet).taskCandidateOrAssigned(loginUser.getUsername()).endOr();
            } else {
                // 按照分配组 OR 指派人查询
                query.taskCandidateOrAssigned(loginUser.getUsername());
            }
        }
    }


    /**
     * 获取当前登录用户
     */
    public LoginUser checkLogin() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        if (Objects.isNull(user)) {
            throw new AiurtBootException("请重新登录");
        }
        return user;
    }

    /**
     * 验证流程的实时任务信息。
     *
     * @param task 流程引擎的任务对象。
     * @return 任务信息对象。
     */
    public void verifyAndGetRuntimeTaskInfo(Task task) {
        if (task == null) {
            throw new AiurtBootException("数据验证失败，请核对指定的任务Id，请刷新后重试！");
        }
        if (!this.isAssigneeOrCandidate(task)) {
            throw new AiurtBootException("数据验证失败，当前用户不是指派人也不是候选人之一！");
        }
        if (StrUtil.isBlank(task.getFormKey())) {
            throw new AiurtBootException("数据验证失败，指定任务的formKey属性不存在，请重新修改流程图！");
        }
    }

    /**
     * 获取流程实例的列表。
     *
     * @param processInstanceIdSet 流程实例Id集合。
     * @return 流程实例列表。
     */
    @Override
    public List<ProcessInstance> getProcessInstanceList(Set<String> processInstanceIdSet) {
        return runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIdSet).list();
    }

    /**
     * 获取流程定义的列表。
     *
     * @param processDefinitionIdSet 流程定义Id集合。
     * @return 流程定义列表。
     */
    @Override
    public List<ProcessDefinition> getProcessDefinitionList(Set<String> processDefinitionIdSet) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionIds(processDefinitionIdSet).list();
    }
}
