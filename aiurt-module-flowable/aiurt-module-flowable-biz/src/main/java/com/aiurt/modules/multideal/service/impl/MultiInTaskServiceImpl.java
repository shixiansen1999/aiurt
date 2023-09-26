package com.aiurt.modules.multideal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.common.constant.FlowVariableConstant;
import com.aiurt.modules.common.enums.MultiApprovalRuleEnum;
import com.aiurt.modules.flow.constants.FlowApprovalType;
import com.aiurt.modules.flow.dto.ProcessParticipantsInfoDTO;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.aiurt.modules.flow.service.IActCustomTaskCommentService;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.multideal.dto.AddReduceMultiInstanceDTO;
import com.aiurt.modules.multideal.entity.ActCustomMultiRecord;
import com.aiurt.modules.multideal.service.IActCustomMultiRecordService;
import com.aiurt.modules.multideal.service.IMultiInTaskService;
import com.aiurt.modules.multideal.service.IMultiInstanceUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysUserModel;
import org.jetbrains.annotations.NotNull;
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
public class MultiInTaskServiceImpl implements IMultiInTaskService {

    @Autowired
    private IActCustomTaskExtService taskExtService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IMultiInstanceUserService multiInstanceUserService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FlowElementUtil flowElementUtil;

    @Autowired
    private IActCustomMultiRecordService multiRecordService;

    @Autowired
    private IActCustomTaskCommentService taskCommentService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;


    /**
     * 判断是否为多实例任务
     *
     * @param task
     * @return
     */
    @Override
    public Boolean isMultiInTask(Task task) {
        String nodeId = task.getTaskDefinitionKey();
        String definitionId = task.getProcessDefinitionId();
        ActCustomTaskExt actCustomTaskExt = taskExtService.getByProcessDefinitionIdAndTaskId(definitionId, nodeId);
        if (Objects.isNull(actCustomTaskExt)) {
            log.info("没有查询节点（{}）流转属性配置", nodeId);
            return false;
        }

        // 获取节点的用户
        List<String> currentUserList = multiInstanceUserService.getCurrentUserList(task.getId());

        if (CollUtil.isEmpty(currentUserList)) {
            log.info("当前任务不是多实例，taskId：{}", task.getId());
            return false;
        }
        return true;
    }

    /**
     * 判断是否多实例任务且不是多实例的最后一步
     * 是多实例任务且不是多实例的最后一步 返回true
     * 否则返回false
     *
     * @param task
     * @return
     */
    @Override
    public Boolean areMultiInTask(Task task) {
        String nodeId = task.getTaskDefinitionKey();
        String definitionId = task.getProcessDefinitionId();
        ActCustomTaskExt actCustomTaskExt = taskExtService.getByProcessDefinitionIdAndTaskId(definitionId, nodeId);
        if (Objects.isNull(actCustomTaskExt)) {
            log.info("没有查询节点（{}）流转属性配置", nodeId);
            return false;
        }
        String userType = actCustomTaskExt.getUserType();
        if (StrUtil.isBlank(userType)) {
            return false;
        }
        MultiApprovalRuleEnum approvalRuleEnum = MultiApprovalRuleEnum.getByCode(userType);
        if (Objects.isNull(approvalRuleEnum)) {
            return false;
        }

        switch (approvalRuleEnum) {
            case TASK_MULTI_INSTANCE_TYPE_3:
                return areSerialMultiInTask(task);
            case TASK_MULTI_INSTANCE_TYPE_2:
                return areParallelMultiInTask(task);
            case TASK_MULTI_INSTANCE_TYPE_1:
                return false;
            default:
                return false;
        }
    }

    /**
     * 判断是否为多实例任务
     *
     * @param nodeId
     * @param definitionId
     * @return
     */
    @Override
    public Boolean isMultiInTask(String nodeId, String definitionId) {
        ActCustomTaskExt actCustomTaskExt = taskExtService.getByProcessDefinitionIdAndTaskId(definitionId, nodeId);
        if (Objects.isNull(actCustomTaskExt)) {
            log.info("没有查询节点（{}）流转属性配置", nodeId);
            return false;
        }
        String userType = actCustomTaskExt.getUserType();
        if (StrUtil.isBlank(userType)) {
            return false;
        }
        MultiApprovalRuleEnum approvalRuleEnum = MultiApprovalRuleEnum.getByCode(userType);
        if (Objects.isNull(approvalRuleEnum)) {
            return false;
        }
        return true;
    }

    /**
     * 判断当前任务是否需要提交了
     *
     * @param task
     * @return
     */
    @Override
    public Boolean isCompleteTask(Task task) {
        // 判断是否多实例的最后一步， 如果不是最后一个不需要提交, 但是不是多实例的时候需要提交，需要设置流程办理人
        Boolean areMultiInTask = this.areMultiInTask(task);
        // 判断当前任务是否为多实例任务
        Boolean isMultiInTask = this.isMultiInTask(task);
        //  不是多是任务提交,
        Boolean flag = !isMultiInTask ;
        // 多实例, 多实例最后一步需要为ture，执行自从选人
        if (isMultiInTask) {
            flag = !areMultiInTask;
        }
        return flag;
    }

    /**
     * 加签
     *
     * @param addReduceMultiInstanceDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMultiInstance(AddReduceMultiInstanceDTO addReduceMultiInstanceDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String reason = addReduceMultiInstanceDTO.getReason();
        String taskId = addReduceMultiInstanceDTO.getTaskId();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new AiurtBootException(AiurtErrorEnum.FLOW_TASK_NOT_FOUND.getCode(), AiurtErrorEnum.FLOW_TASK_NOT_FOUND.getMessage());
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();
        UserTask userTaskModel = (UserTask) flowElementUtil.getFlowElement(task.getProcessDefinitionId(), taskDefinitionKey);

        MultiInstanceLoopCharacteristics loopCharacteristics = userTaskModel.getLoopCharacteristics();
        if (Objects.isNull(loopCharacteristics)) {
            return;
        }
        String elementVariable = loopCharacteristics.getElementVariable();
        if (StrUtil.isBlank(elementVariable)) {
            return;
        }
        String variableName = FlowVariableConstant.ASSIGNEE_LIST + taskDefinitionKey;
        List<String> list = taskService.getVariable(taskId, variableName, List.class);
        List<String> addAssigneeVariables = taskService.getVariable(taskId, FlowVariableConstant.ADD_ASSIGNEE_LIST + taskDefinitionKey, List.class);

        List<String> userNameList = addReduceMultiInstanceDTO.getUserNameList();
        if (CollUtil.isNotEmpty(list)) {
            list.addAll(userNameList);
        }
        if (Objects.isNull(addAssigneeVariables)) {
            addAssigneeVariables = userNameList;
        }else {
            addAssigneeVariables.addAll(userNameList);
        }
        //
        Map<String, Object> executionVariables = new HashMap<>(4);
        List<ActCustomMultiRecord> recordList = new ArrayList<>();
        userNameList.stream().forEach(userName->{
            // 设置局部变量, 并行实例必须采用该变量设置, 加签标识，否则去重
            executionVariables.put(elementVariable, userName);
            executionVariables.put("is_multi_assign_task", true);
            // 执行
            runtimeService.addMultiInstanceExecution(taskDefinitionKey, task.getProcessInstanceId(), executionVariables);
            ActCustomMultiRecord build = ActCustomMultiRecord.builder()
                    .delFlag(0)
                    .executionId(task.getExecutionId())
                    .processInstanceId(task.getProcessInstanceId())
                    .taskId(taskId)
                    .userName(task.getAssignee())
                    .mutilUserName(userName)
                    .nodeId(taskDefinitionKey)
                    .reason(reason).build();
            recordList.add(build);
        });
        // 重新设置多实例人员集合变量的列表值
        taskService.setVariable(taskId, variableName, list);

        // 设置加签的数据
        taskService.setVariable(taskId, FlowVariableConstant.ADD_ASSIGNEE_LIST + taskDefinitionKey, addAssigneeVariables);

        // 添加加签记录
        multiRecordService.saveBatch(recordList);

        // 加签记录
        ActCustomTaskComment flowTaskComment = new ActCustomTaskComment();
        flowTaskComment.fillWith(task);
        flowTaskComment.setApprovalType(FlowApprovalType.ADD_MULTI);
        flowTaskComment.setComment(reason);
        flowTaskComment.setCreateRealname(loginUser.getRealname());
        taskCommentService.getBaseMapper().insert(flowTaskComment);

    }

    /**
     * 减签
     *
     * @param addReduceMultiInstanceDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reduceMultiInstance(AddReduceMultiInstanceDTO addReduceMultiInstanceDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String reason = addReduceMultiInstanceDTO.getReason();
        String taskId = addReduceMultiInstanceDTO.getTaskId();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new AiurtBootException(AiurtErrorEnum.FLOW_TASK_NOT_FOUND.getCode(), AiurtErrorEnum.FLOW_TASK_NOT_FOUND.getMessage());
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();
        UserTask userTaskModel = (UserTask) flowElementUtil.getFlowElement(task.getProcessDefinitionId(), taskDefinitionKey);

        MultiInstanceLoopCharacteristics loopCharacteristics = userTaskModel.getLoopCharacteristics();
        if (Objects.isNull(loopCharacteristics)) {
            return;
        }

        String elementVariable = loopCharacteristics.getElementVariable();
        if (StrUtil.isBlank(elementVariable)) {
            return;
        }

        List<String> addAssigneeVariables = taskService.getVariable(taskId, FlowVariableConstant.ADD_ASSIGNEE_LIST + taskDefinitionKey, List.class);
        if (CollUtil.isEmpty(addAssigneeVariables)) {
            return;
        }


        List<String> userNameList = addReduceMultiInstanceDTO.getUserNameList();
        //  代办的任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(taskDefinitionKey).active().list();
        // 删除的任务
        List<Task> deleteTaskList = taskList.stream().filter(t -> userNameList.contains(t.getAssignee())).collect(Collectors.toList());

        // 修改变量
        String variableName = FlowVariableConstant.ASSIGNEE_LIST + taskDefinitionKey;
        List<String> assigneeVariables = taskService.getVariable(taskId, variableName, List.class);

        if (CollUtil.isNotEmpty(assigneeVariables)) {
            assigneeVariables.removeAll(userNameList);
        }
        addAssigneeVariables.removeAll(userNameList);
        // 重新设置多实例人员集合变量的列表值
        taskService.setVariable(taskId, variableName, assigneeVariables);
        taskService.setVariable(taskId,  FlowVariableConstant.ADD_ASSIGNEE_LIST + taskDefinitionKey, addAssigneeVariables);
        deleteTaskList.stream().forEach(t->{
            runtimeService.deleteMultiInstanceExecution(t.getExecutionId(), true);
        });
        // 6.4.2 是存在其他问题的
        // 如果是串行多实例减签操作，针对 Flowable6.4.2 有BUG问题，做如下处理
        if (userTaskModel.getLoopCharacteristics().isSequential()) {
            Set<String> taskIds =
                    taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(task.getTaskDefinitionKey())
                            .list().stream().map(Task::getId).collect(Collectors.toSet());
            taskIds.remove(task.getId());

            // 部分控制变量需要修改值
            Execution execution = runtimeService.createExecutionQuery().executionId(
                    task.getExecutionId()).singleResult();
            if (execution != null && CollUtil.isNotEmpty(assigneeVariables)) {
                Map<String, Object> executionVariables = new HashMap<>(2);
                executionVariables.put("nrOfInstances", assigneeVariables.size());
                runtimeService.setVariables(execution.getParentId(), executionVariables);

                //deleteExecutionById(form.getDeleteExecutionId());
            }
        }
    }

    /**
     * 查询减签的人员信息
     *
     * @param taskId
     * @return
     */
    @Override
    public List<ProcessParticipantsInfoDTO> getReduceMultiUser(String taskId) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 只能放在中间变量中了， 提交的是否删除
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            log.error("该任务已结束或者不存在taskId->{}", taskId);
            return Collections.emptyList();
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String variableName = FlowVariableConstant.ADD_ASSIGNEE_LIST + taskDefinitionKey;
        List<String> list = taskService.getVariable(taskId, variableName, List.class);
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 查询待办任务，删除,
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(taskDefinitionKey).list();

        List<String> userAssigneeList = taskList.stream().map(Task::getAssignee).collect(Collectors.toList());
        list.retainAll(userAssigneeList);
        list.remove(loginUser.getUsername());
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<ProcessParticipantsInfoDTO> resultList = new ArrayList<>();
        ProcessParticipantsInfoDTO processParticipantsInfoDTO = new ProcessParticipantsInfoDTO();
        processParticipantsInfoDTO.setTitle(task.getName());
        processParticipantsInfoDTO.setNodeId(task.getId());
        processParticipantsInfoDTO.setOptions(new ArrayList<>());
        buildUserParticipantsInfo(list, processParticipantsInfoDTO.getOptions());
        resultList.add(processParticipantsInfoDTO);
        return resultList;
    }

    /**
     * 构建用户维度的流程参与者信息
     *
     * @param userNameStr 用户字符创
     * @param result     结果列表，用于存储构建的参与者信息对象
     */
    private void buildUserParticipantsInfo(List<String> userNameStr, List<ProcessParticipantsInfoDTO> result) {
        if(CollUtil.isEmpty(userNameStr)){
            return;
        }

        List<LoginUser> loginUsers = sysBaseAPI.getLoginUserList(userNameStr);
        if (CollUtil.isEmpty(loginUsers)) {
            return;
        }

        List<SysUserModel> data = CollUtil.newArrayList();
        for (LoginUser loginUser : loginUsers) {
            SysUserModel sysUserModel = buildSysUserModel(loginUser);
            if (ObjectUtil.isNotEmpty(sysUserModel)) {
                data.add(sysUserModel);
            }
        }

        if (CollUtil.isNotEmpty(data)) {
            ProcessParticipantsInfoDTO processParticipantsInfoDTO = new ProcessParticipantsInfoDTO();
            processParticipantsInfoDTO.setTitle("用户");
            processParticipantsInfoDTO.setData(data);
            result.add(processParticipantsInfoDTO);
        }
    }

    @NotNull
    private SysUserModel buildSysUserModel(LoginUser loginUser) {
        SysUserModel sysUserModel = new SysUserModel();
        sysUserModel.setId(loginUser.getId());
        sysUserModel.setKey(loginUser.getId());
        sysUserModel.setLabel(loginUser.getRealname());
        sysUserModel.setValue(loginUser.getUsername());
        sysUserModel.setAvatar(loginUser.getAvatar());
        sysUserModel.setOrgName(loginUser.getOrgName());
        sysUserModel.setPostName(loginUser.getPostNames());
        sysUserModel.setRoleName(loginUser.getRoleNames());
        return sysUserModel;
    }

    /**
     * 多实例-并行
     * @param task
     * @return
     */
    private Boolean areParallelMultiInTask(Task task) {
        String nodeId = task.getTaskDefinitionKey();
        String pInstanceId = task.getProcessInstanceId();
        String executionId = task.getExecutionId();
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(pInstanceId).
                activityId(nodeId).list();
        if (CollectionUtils.isEmpty(executions)){
            return false;
        }
        return executions.stream().filter(execution -> !StringUtils.equals(execution.getId(),executionId)).
                anyMatch(execution -> !execution.isEnded());
    }

    /**
     * 串行
     * @param task
     * @return
     */
    private Boolean areSerialMultiInTask(Task task) {
        String taskId = task.getId();
        String nodeId = task.getTaskDefinitionKey();
        List<String> currentUserList = multiInstanceUserService.getCurrentUserList(taskId);
        if (CollUtil.isEmpty(currentUserList) || currentUserList.size() == 1) {
            log.info("活动（{}）,节点（{}），是单实例", taskId, nodeId);
            return false;
        }

        Integer nrOfCompletedInstances = taskService.getVariable(taskId, FlowVariableConstant.LOOP_COUNTER, Integer.class);
        if (nrOfCompletedInstances == null){
            log.info("活动（{}）,节点（{}），不是多单实例", taskId, nodeId);
            return false;
        }

        if (nrOfCompletedInstances < currentUserList.size() -1) {
            log.info("活动（{}）,节点（{}），是多实例且不是最后一个多实例任务", taskId, nodeId);
            return true;
        }
        return false;
    }


}
