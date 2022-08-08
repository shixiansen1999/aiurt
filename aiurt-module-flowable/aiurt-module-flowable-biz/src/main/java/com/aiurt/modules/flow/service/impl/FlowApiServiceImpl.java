package com.aiurt.modules.flow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.flow.dto.*;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.flow.service.IActCustomTaskCommentService;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.utils.ReflectionService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import liquibase.pro.packaged.O;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
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
    private HistoryService historyService;
    @Autowired
    private IActCustomTaskCommentService customTaskCommentService;
    @Autowired
    private IActCustomTaskExtService customTaskExtService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private ModelService modelService;


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
        String businessKey = saveBusData(result.getId(), task.getTaskDefinitionKey());

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
        List<ActCustomTaskExt> actCustomTaskExts = customTaskExtService.getBaseMapper().selectList(
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
            customTaskCommentService.getBaseMapper().insert(comment);
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
    @Override
    public Task getProcessInstanceActiveTask(String processInstanceId, String taskId) {
        TaskQuery query = taskService.createTaskQuery().processInstanceId(processInstanceId);
        if (StrUtil.isNotBlank(taskId)) {
            query.taskId(taskId);
        }
        return query.active().singleResult();
    }

    /**
     * 获取流程运行时指定任务的信息。
     *
     * @param processDefinitionId 流程引擎的定义Id。
     * @param processInstanceId   流程引擎的实例Id。
     * @param taskId              流程引擎的任务Id。
     * @return 任务节点的自定义对象数据。
     */
    @Override
    public TaskInfoDTO viewRuntimeTaskInfo(String processDefinitionId, String processInstanceId, String taskId) {
        TaskInfoDTO taskInfoDTO = new TaskInfoDTO();
        Task task = this.getProcessInstanceActiveTask(processInstanceId, taskId);
        if (task == null) {
            throw new AiurtBootException("数据验证失败，指定的任务Id，请刷新后重试！");
        }
        if (!this.isAssigneeOrCandidate(task)) {
            throw new AiurtBootException("数据验证失败，当前用户不是指派人也不是候选人之一！");
        }

        ActCustomTaskExt flowTaskExt =
                customTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, task.getTaskDefinitionKey());
        if (flowTaskExt != null) {
            if (StrUtil.isNotBlank(flowTaskExt.getOperationListJson())) {
                taskInfoDTO.setOperationList(JSON.parseArray(flowTaskExt.getOperationListJson(), JSONObject.class));
            }
            if (StrUtil.isNotBlank(flowTaskExt.getVariableListJson())) {
                taskInfoDTO.setVariableList(JSON.parseArray(flowTaskExt.getVariableListJson(), JSONObject.class));
            }
        }
        return taskInfoDTO;
    }

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
    @Override
    public IPage<FlowHisTaskDTO> listHistoricTask(String processDefinitionName, String beginDate, String endDate, Integer pageNo, Integer pageSize) {
        // 查询已办任务
        Page<HistoricTaskInstance> hisTaskFinishList = this.getHistoricTaskInstanceFinishedList(processDefinitionName, beginDate, endDate, pageNo, pageSize);

        // 对象转换
        IPage<FlowHisTaskDTO> result = new Page<>();
        List<FlowHisTaskDTO> flowHisTaskDTOS = new ArrayList<>();
        List<HistoricTaskInstance> records = hisTaskFinishList.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            records.forEach(re -> {
                FlowHisTaskDTO flowHisTaskDTO = new FlowHisTaskDTO();
                flowHisTaskDTO.setId(re.getId());
                flowHisTaskDTO.setProcessDefinitionId(re.getProcessDefinitionId());
                flowHisTaskDTO.setProcessInstanceStartTime(re.getCreateTime());
                flowHisTaskDTO.setTaskName(re.getName());
                flowHisTaskDTO.setFormKey(re.getFormKey());
            });
        }

        // 封装流程定义、名称等相关信息
        if (CollUtil.isNotEmpty(records)) {
            Set<String> instanceIdSet = records.stream()
                    .map(HistoricTaskInstance::getProcessInstanceId).collect(Collectors.toSet());
            List<HistoricProcessInstance> instanceList = this.getHistoricProcessInstanceList(instanceIdSet);
            Map<String, HistoricProcessInstance> instanceMap =
                    instanceList.stream().collect(Collectors.toMap(HistoricProcessInstance::getId, c -> c));
            flowHisTaskDTOS.forEach(flowHisTaskDTO -> {
                HistoricProcessInstance instance = instanceMap.get(flowHisTaskDTO.getProcessInstanceId());
                flowHisTaskDTO.setProcessDefinitionKey(instance.getProcessDefinitionKey());
                flowHisTaskDTO.setProcessDefinitionName(instance.getProcessDefinitionName());
                flowHisTaskDTO.setStartUser(instance.getStartUserId());
                flowHisTaskDTO.setBusinessKey(instance.getBusinessKey());
            });

            // 封装审批类型
            Set<String> taskIdSet =
                    records.stream().map(HistoricTaskInstance::getId).collect(Collectors.toSet());
            List<ActCustomTaskComment> commentList = customTaskCommentService.getFlowTaskCommentListByTaskIds(taskIdSet);
            Map<String, List<ActCustomTaskComment>> commentMap =
                    commentList.stream().collect(Collectors.groupingBy(ActCustomTaskComment::getTaskId));
            flowHisTaskDTOS.forEach(flowHisTaskDTO -> {
                List<ActCustomTaskComment> comments = commentMap.get(flowHisTaskDTO.getId());
                if (CollUtil.isNotEmpty(comments)) {
                    flowHisTaskDTO.setApprovalType(comments.get(0).getApprovalType());
                    comments.remove(0);
                }
            });
        }

        result.setRecords(flowHisTaskDTOS);
        result.setTotal(hisTaskFinishList.getTotal());
        result.setCurrent(hisTaskFinishList.getCurrent());
        result.setPages(hisTaskFinishList.getPages());
        result.setSize(hisTaskFinishList.getSize());
        return null;
    }

    /**
     * 获取流程实例的历史流程实例列表。
     *
     * @param processInstanceIdSet 流程实例Id集合。
     * @return 历史流程实例列表。
     */
    public List<HistoricProcessInstance> getHistoricProcessInstanceList(Set<String> processInstanceIdSet) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIdSet).list();
    }

    /**
     * 获取已办任务
     *
     * @param processDefinitionName 流程名称
     * @param beginDate             开始时间
     * @param endDate               结束时间
     * @param pageNo                当前页
     * @param pageSize              每页数量
     * @return
     */
    private Page<HistoricTaskInstance> getHistoricTaskInstanceFinishedList(String processDefinitionName, String beginDate, String endDate, Integer pageNo, Integer pageSize) {
        Page<HistoricTaskInstance> result = new Page<>();
        String username = checkLogin().getUsername();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(username)
                .finished();
        if (StrUtil.isNotBlank(processDefinitionName)) {
            query.processDefinitionName(processDefinitionName);
        }
        if (StrUtil.isNotBlank(beginDate)) {
            query.taskCompletedAfter(DateUtil.parse(beginDate, "yyyy-MM-dd HH:mm:ss"));
        }
        if (StrUtil.isNotBlank(endDate)) {
            query.taskCompletedBefore(DateUtil.parse(endDate, "yyyy-MM-dd HH:mm:ss"));
        }
        query.orderByHistoricTaskInstanceEndTime().desc();
        long totalCount = query.count();
        int firstResult = (pageNo - 1) * pageSize;
        List<HistoricTaskInstance> instanceList = query.listPage(firstResult, pageSize);
        result.setRecords(instanceList);
        result.setCurrent(pageNo);
        result.setSize(pageSize);
        result.setTotal(totalCount);
        result.setPages(totalCount <= 0 ? 0 : (totalCount > 1 ? (totalCount - 1) / pageSize + 1 : 1));
        return result;
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
        result.setPages(totalCount <= 0 ? 0 : (totalCount > 1 ? (totalCount - 1) / pageSize + 1 : 1));
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

    /**
     * 获取指定流程定义的流程图
     *
     * @param processDefinitionId 流程定义Id
     * @return
     */
    @Override
    public BpmnModel getBpmnModelByDefinitionId(String processDefinitionId) {
        return repositoryService.getBpmnModel(processDefinitionId);
    }

    /**
     * 获取流程实例的历史流程实例。
     *
     * @param processInstanceId 流程实例Id。
     * @return 历史流程实例。
     */
    @Override
    public HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    /**
     * 获取流程图高亮数据。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程图高亮数据。
     */
    @Override
    public HighLightedNodeDTO viewHighlightFlowData(String processInstanceId) {

        HistoricProcessInstance hpi = this.getHistoricProcessInstance(processInstanceId);
        BpmnModel bpmnModel = this.getBpmnModelByDefinitionId(hpi.getProcessDefinitionId());
        //Process对象集合
        List<Process> processList = bpmnModel.getProcesses();
        List<FlowElement> flowElementList = new LinkedList<>();
        processList.forEach(p -> flowElementList.addAll(p.getFlowElements()));
        Map<String, String> allSequenceFlowMap = new HashMap<>(16);

        //连线信息
        for (FlowElement flowElement : flowElementList) {
            if (flowElement instanceof SequenceFlow) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                String ref = sequenceFlow.getSourceRef();
                String targetRef = sequenceFlow.getTargetRef();
                allSequenceFlowMap.put(ref + targetRef, sequenceFlow.getId());
            }
        }

        //获取流程实例的历史节点(全部执行过的节点，被拒绝的任务节点将会出现多次)
        Set<String> finishedTaskSet = new LinkedHashSet<>();
        List<HistoricActivityInstance> activityInstanceList =
                this.getHistoricActivityInstanceList(processInstanceId);
        List<String> activityInstanceTask = activityInstanceList.stream()
                .filter(s -> !StrUtil.equals(s.getActivityType(), "sequenceFlow"))
                .map(HistoricActivityInstance::getActivityId).collect(Collectors.toList());
        Set<String> finishedTaskSequenceSet = new LinkedHashSet<>();
        for (int i = 0; i < activityInstanceTask.size(); i++) {
            String current = activityInstanceTask.get(i);
            if (i != activityInstanceTask.size() - 1) {
                String next = activityInstanceTask.get(i + 1);
                finishedTaskSequenceSet.add(current + next);
            }
            finishedTaskSet.add(current);
        }
        Set<String> finishedSequenceFlowSet = new HashSet<>();
        finishedTaskSequenceSet.forEach(s -> finishedSequenceFlowSet.add(allSequenceFlowMap.get(s)));

        //获取流程实例当前正在待办的节点
        List<HistoricActivityInstance> unfinishedInstanceList =
                this.getHistoricUnfinishedInstanceList(processInstanceId);
        Set<String> unfinishedTaskSet = new LinkedHashSet<>();
        for (HistoricActivityInstance unfinishedActivity : unfinishedInstanceList) {
            unfinishedTaskSet.add(unfinishedActivity.getActivityId());
        }

        byte[] bpmnXML = modelService.getBpmnXML(bpmnModel);
        String modelXml = new String(bpmnXML, StandardCharsets.UTF_8);
        HighLightedNodeDTO highLightedNodeDTO = HighLightedNodeDTO.builder()
                .finishedTaskSet(finishedTaskSet)
                .finishedSequenceFlowSet(finishedSequenceFlowSet)
                .unfinishedTaskSet(unfinishedTaskSet)
                .modelName(hpi.getProcessDefinitionName())
                .modelXml(modelXml)
                .build();
        return highLightedNodeDTO;
    }

    /**
     * 获取流程实例的已完成历史任务列表。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例已完成的历史任务列表。
     */
    @Override
    public List<HistoricActivityInstance> getHistoricActivityInstanceList(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
    }

    /**
     * 获取流程实例的待完成任务列表。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例待完成的任务列表。
     */
    @Override
    public List<HistoricActivityInstance> getHistoricUnfinishedInstanceList(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).unfinished().list();
    }

    /**
     * 创建用户任务监听器
     * @param userTask
     * @param listenerClazz
     */
    @Override
    public void addTaskCreateListener(UserTask userTask, Class<? extends TaskListener> listenerClazz) {
        Assert.notNull(listenerClazz);
        FlowableListener flowableListener = new FlowableListener();
        flowableListener.setEvent("create");
        flowableListener.setImplementationType("class");
        flowableListener.setImplementation(listenerClazz.getName());
        userTask.getTaskListeners().add(flowableListener);
    }

    /**
     * 流程实例
     * @param reqDTO
     * @return
     */
    @Override
    public IPage<HistoricProcessInstanceDTO> listAllHistoricProcessInstance(HistoricProcessInstanceReqDTO reqDTO) {

        IPage<HistoricProcessInstanceDTO> pages = new Page<>();
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();

        if (Objects.nonNull(reqDTO.getStartTime())) {
            DateTime dateTime = DateUtil.beginOfDay(reqDTO.getStartTime());
            query.startedAfter(dateTime);
        }

        if (Objects.nonNull(reqDTO.getEndTime())) {
            DateTime dateTime = DateUtil.endOfDay(reqDTO.getEndTime());
            query.startedBefore(dateTime);
        }
        if (StrUtil.isNotBlank(reqDTO.getLoginName())) {
            query.startedBy(reqDTO.getLoginName());
        }

        query.orderByProcessInstanceStartTime().desc();

        long count = query.count();

        int firstResult = (reqDTO.getPageNo() - 1) * reqDTO.getPageSize();

        List<HistoricProcessInstance> instanceList = query.listPage(firstResult, reqDTO.getPageSize());

        List<HistoricProcessInstanceDTO> instanceDTOList = instanceList.stream().map(historicProcessInstance -> {
            // 用户名处理
            String startUserId = historicProcessInstance.getStartUserId();
            LoginUser userByName = sysBaseAPI.getUserByName(startUserId);
            String realName = startUserId;
            if (Objects.nonNull(userByName)) {
                realName = userByName.getRealname();
            }
            return HistoricProcessInstanceDTO.builder()
                    .businessKey(historicProcessInstance.getBusinessKey())
                    .startTime(historicProcessInstance.getStartTime())
                    .name(historicProcessInstance.getName())
                    .processDefinitionName(historicProcessInstance.getProcessDefinitionName())
                    .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
                    .endTime(historicProcessInstance.getEndTime())
                    .durationInMillis(historicProcessInstance.getDurationInMillis())
                    .processInstanceId(historicProcessInstance.getProcessDefinitionId())
                    .processDefinitionKey(historicProcessInstance.getProcessDefinitionKey())
                    .realName(realName)
                    .userName(startUserId).build();

        }).collect(Collectors.toList());

        pages.setCurrent(reqDTO.getPageNo());

        pages.setRecords(instanceDTOList);
        pages.setTotal(count);
        return pages;
    }


    /**
     * 终止流程
     * @param instanceDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stopProcessInstance(StopProcessInstanceDTO instanceDTO) {
        List<Task> list = taskService.createTaskQuery().processDefinitionId(instanceDTO.getProcessInstanceId()).active().list();

        if (CollUtil.isEmpty(list)) {
            throw new AiurtBootException("当前流程尚未开始或已经结束！");
        }

        for (Task task : list) {
            // 流程定义id
            String processDefinitionId = task.getProcessDefinitionId();
            // 任务定义id
            String taskDefinitionKey = task.getTaskDefinitionKey();
            // 结束节点
            EndEvent endEvent = flowElementUtil.getEndEvent(processDefinitionId);

            // 流程跳转, flowable 已提供
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(instanceDTO.getProcessInstanceId())
                    .moveActivityIdTo(taskDefinitionKey, endEvent.getId())
                    .changeState();
        }
    }

    /**
     * 删除流程
     * @param processInstanceId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProcessInstance(String processInstanceId) {
        historyService.deleteHistoricProcessInstance(processInstanceId);
    }
}
