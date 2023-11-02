package com.aiurt.modules.flow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.BetweenFormater;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import com.aiurt.modules.common.constant.FlowVariableConstant;
import com.aiurt.modules.complete.dto.FlowCompleteReqDTO;
import com.aiurt.modules.complete.service.impl.CommonFlowTaskCompleteServiceImpl;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.copy.service.IActCustomProcessCopyService;
import com.aiurt.modules.deduplicate.handler.BackNodeRuleVerifyHandler;
import com.aiurt.modules.flow.constants.FlowApprovalType;
import com.aiurt.modules.flow.dto.*;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.aiurt.modules.flow.enums.FlowStatesEnum;
import com.aiurt.modules.flow.mapper.ActCustomTaskCommentMapper;
import com.aiurt.modules.flow.mapper.FlowApiServiceMapper;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.flow.service.IActCustomFlowStateService;
import com.aiurt.modules.flow.service.IActCustomTaskCommentService;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.forecast.dto.HistoryTaskInfo;
import com.aiurt.modules.forecast.service.IFlowForecastService;
import com.aiurt.modules.modeler.entity.*;
import com.aiurt.modules.modeler.service.IActCustomModelExtService;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.modeler.service.IActCustomVariableService;
import com.aiurt.modules.multideal.service.IActCustomMultiRecordService;
import com.aiurt.modules.multideal.service.IMultiInTaskService;
import com.aiurt.modules.online.businessdata.entity.ActCustomBusinessData;
import com.aiurt.modules.online.businessdata.service.IActCustomBusinessDataService;
import com.aiurt.modules.online.page.entity.ActCustomPage;
import com.aiurt.modules.online.page.service.IActCustomPageService;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.enums.EmptyRuleEnum;
import com.aiurt.modules.user.getuser.service.DefaultSelectUserService;
import com.aiurt.modules.user.service.IActCustomUserService;
import com.aiurt.modules.user.service.IFlowUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.*;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ChangeActivityStateBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysUserModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
    protected IdentityService identityService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private ModelService modelService;

    @Autowired
    private ActCustomTaskCommentMapper actCustomTaskCommentMapper;

    @Autowired
    private IActCustomPageService pageService;

    @Autowired
    private IActCustomBusinessDataService businessDataService;

    @Autowired
    private IFlowUserService flowUserService;

    @Autowired
    @Lazy
    private IActCustomModelInfoService modelInfoService;

    @Autowired
    private IActCustomVariableService variableService;

    @Autowired
    @Lazy
    private ISTodoBaseAPI todoBaseApi;

    @Autowired
    private IActCustomUserService actCustomUserService;

    @Autowired
    private CommonFlowTaskCompleteServiceImpl commonFlowTaskCompleteService;

    @Autowired
    private IMultiInTaskService multiInTaskService;

    @Autowired
    private DefaultSelectUserService relationSelectUser;

    @Autowired
    private IActCustomModelExtService modelExtService;

    @Autowired
    private IActCustomProcessCopyService actCustomProcessCopyService;


    @Autowired
    private IFlowForecastService flowForecastService;

    @Autowired
    private IActCustomTaskCommentService taskCommentService;

    @Autowired
    private FlowApiServiceMapper flowApiServiceMapper;

    @Autowired
    private IActCustomMultiRecordService multiRecordService;

    @Autowired
    private IActCustomFlowStateService flowStateService;

    private static final int TWO_SIZE = 2;

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

        // 验证流程定义数据的合法性。
        Result<ProcessDefinition> processDefinitionResult = flowElementUtil.verifyAndGetFlowEntry(startBpmnDTO.getModelKey());
        if (!processDefinitionResult.isSuccess()) {
            throw new AiurtBootException(processDefinitionResult.getMessage());
        }

        ProcessDefinition result = processDefinitionResult.getResult();
        if (result.isSuspended()) {
            throw new AiurtBootException("当前流程定义已被挂起，不能启动新流程！");
        }
        // 设置流程变量
        Map<String, Object> busData = startBpmnDTO.getBusData();
        this.initAndGetProcessInstanceVariables(busData);

        // 根据key查询第一个用户任务
        UserTask userTask = flowElementUtil.getFirstUserTaskByModelKey(startBpmnDTO.getModelKey());

        // 保存中间业务数据，将业务数据id返回
        Object businessKey = flowElementUtil.saveBusData(result.getId(), userTask.getId(), busData);

        String loginName = loginUser.getUsername();
        Authentication.setAuthenticatedUserId(loginName);

        Map<String, Object> variableMap = new HashMap<>(16);
        variableMap.put(FlowConstant.PROC_INSTANCE_INITIATOR_VAR, loginUser.getUsername());
        variableMap.put(FlowConstant.PROC_INSTANCE_START_USER_NAME_VAR, loginUser.getUsername());

        // 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(result.getId(),Objects.isNull(businessKey)?null:(String)businessKey , busData);

        flowStateService.updateFlowState(processInstance.getProcessInstanceId(), FlowStatesEnum.UN_COMPLETE.getCode());
        log.info("启动流程成功！");

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
    public void startAndTakeFirst(StartBpmnDTO startBpmnDTO) {

        log.info("启动流程请求参数：[{}]", JSON.toJSONString(startBpmnDTO));
        String loginName = startBpmnDTO.getUserName();
        if (StrUtil.isBlank(loginName)) {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (Objects.isNull(loginUser)) {
                throw new AiurtBootException("无法启动流程，请重新登录！");
            }
            loginName = loginUser.getUsername();
        }

        // 验证流程定义数据的合法性。
        Result<ProcessDefinition> processDefinitionResult = flowElementUtil.verifyAndGetFlowEntry(startBpmnDTO.getModelKey());
        if (!processDefinitionResult.isSuccess()) {
            throw new AiurtBootException(processDefinitionResult.getMessage());
        }

        ProcessDefinition result = processDefinitionResult.getResult();
        if (result.isSuspended()) {
            throw new AiurtBootException("当前程主版本已被挂起，请联系管理员！");
        }
        // 设置流程变量
        Map<String, Object> busData = startBpmnDTO.getBusData();
        Map<String, Object> variableData = new HashMap<>(16);
        this.initAndGetProcessInstanceVariables(variableData);

        Map<String, Object> variables = flowElementUtil.getVariablesByModelKey(busData, startBpmnDTO.getModelKey());
        variableData.putAll(variables);

         // 校验节点是否
        FlowElement startEvent = flowElementUtil.getStartFlowNodeByModelKey(startBpmnDTO.getModelKey());
        List<FlowElement> targetFlowElement = flowElementUtil.getTargetFlowElement(startBpmnDTO.getModelKey(), startEvent, variables);
        if (CollUtil.isEmpty(targetFlowElement)) {
            throw new AiurtBootException("无法找到下一步办理节点，请联系管理员！");
        }
        // 根据key查询第一个用户任务
        UserTask userTask = flowElementUtil.getFirstUserTaskByModelKey(startBpmnDTO.getModelKey());

        // 保存中间业务数据，将业务数据id返回
        Object businessKey = flowElementUtil.saveBusData(result.getId(), userTask.getId(), busData);

        // 保存数据不发起流程
        FlowTaskCompleteCommentDTO flowTaskCompleteDTO = startBpmnDTO.getFlowTaskCompleteDTO();
        if (Objects.nonNull(flowTaskCompleteDTO)
                && StrUtil.equalsAnyIgnoreCase(flowTaskCompleteDTO.getApprovalType(), FlowApprovalType.ONLY_SAVE)) {
            log.info("仅保存数据不发起流程！！！");
            return;
        }

        Authentication.setAuthenticatedUserId(loginName);
        // 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(result.getId(), Objects.isNull(businessKey) ? null : (String) businessKey, variableData);

        // 获取流程启动后的第一个任务。
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();

        // 设置办理人
        taskService.claim(task.getId(), loginName);
        taskService.setAssignee(task.getId(), loginName);
        // 更新代办
        flowStateService.updateFlowState(processInstance.getProcessInstanceId(), FlowStatesEnum.UN_COMPLETE.getCode());

        // 保存数据
        if (Objects.nonNull(busData)) {
            if (StrUtil.isNotBlank((String) businessKey)) {
                busData.put("id", businessKey);
            }
            saveData(task, busData, processInstance.getProcessInstanceId(), task.getId(), processInstance);
        }
        // 完成流程启动后的第一个任务
        if (Objects.nonNull(flowTaskCompleteDTO) && StrUtil.equalsAnyIgnoreCase(flowTaskCompleteDTO.getApprovalType(), FlowApprovalType.AGREE)) {
            // 不需要保存中间业务数据了
            this.completeTask(task, flowTaskCompleteDTO, null, variableData);
        }
    }

    /**
     * 设置流程变量
     *
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
     * 提交任务
     *
     * @param taskCompleteDTO
     */
    @Override
    public void completeTask(TaskCompleteDTO taskCompleteDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 任务id
        String taskId = taskCompleteDTO.getTaskId();
        // 流程实例id
        String processInstanceId = taskCompleteDTO.getProcessInstanceId();

        FlowTaskCompleteCommentDTO flowTaskCompleteDTO = taskCompleteDTO.getFlowTaskCompleteDTO();

        // 获取任务
        Task task = this.getProcessInstanceActiveTask(processInstanceId, taskId);
        if (task == null) {
            throw new AiurtBootException("该任务已被办理！");
        }

        // 设置签收
        String assignee = task.getAssignee();

        if (StrUtil.isNotBlank(assignee) && taskCompleteDTO.getIsCheckAssign()  && !StrUtil.equalsIgnoreCase(loginUser.getUsername(), assignee)) {
            throw new AiurtBootException("该任务已被其他人签收！");
        }
        // 验证流程任务的合法性。
        if (taskCompleteDTO.getIsCheckAssign()) {
            this.verifyAndGetRuntimeTaskInfo(task);
        }


        if (StrUtil.isBlank(assignee)) {
            taskService.setAssignee(taskId, loginUser.getUsername());
        }

        Date claimTime = task.getClaimTime();
        if (Objects.isNull(claimTime)) {
            taskService.claim(taskId, task.getAssignee());
        }

        // 提交任务
        completeTask(task, flowTaskCompleteDTO, taskCompleteDTO.getBusData());
    }

    /**
     * 完成任务，同时提交审批数据。
     *
     * @param task    工作流任务对象。
     * @param comment 审批对象。
     * @param busData 流程任务的变量数据。
     */
    @Override
    public void completeTask(Task task, FlowTaskCompleteCommentDTO comment, Map<String, Object> busData) {
        completeTask(task, comment, busData, new HashMap<>(16));
    }


    /**
     * 提交任务
     * @param task 任务实体
     * @param comment 流转信息
     * @param busData 业务数据
     * @param variableData 变量数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Task task, FlowTaskCompleteCommentDTO comment, Map<String, Object> busData, Map<String, Object> variableData) {
        // 流程实例id
        String processInstanceId = task.getProcessInstanceId();
        // 流程任务id
        String taskId = task.getId();
        // 获取流程任务
        Task processInstanceActiveTask = this.getProcessInstanceActiveTask(processInstanceId, taskId);
        // 获取流程实例对象
        ProcessInstance processInstance = getProcessInstance(processInstanceId);



        // 流程业务数据状态更改
        String approvalType = comment.getApprovalType();
        String commentStr = comment.getComment();
        variableData.put("operationType", approvalType);
        if (StrUtil.equalsIgnoreCase(FlowApprovalType.AUTO_COMPLETE, approvalType)) {
            variableData.put("operationType", FlowApprovalType.AGREE);
        }
        variableData.put("comment", commentStr);

        // 构建中间变量
       // buildVariable(busData, variableData, processInstance);

        ActCustomTaskComment flowTaskComment = BeanUtil.copyProperties(comment, ActCustomTaskComment.class);
        if (flowTaskComment != null && !StrUtil.equalsIgnoreCase(approvalType, FlowApprovalType.CANCEL)) {
            String assignee = task.getAssignee();
            LoginUser loginUser = sysBaseAPI.queryUser(assignee);
            if (Objects.nonNull(loginUser)) {
                flowTaskComment.setCreateRealname(loginUser.getRealname());
            }
            flowTaskComment.fillWith(processInstanceActiveTask);
            customTaskCommentService.save(flowTaskComment);
        }

        // 判断使保存还是提交
        if (StrUtil.equalsIgnoreCase(FlowApprovalType.SAVE, approvalType)) {

            String businessKey = processInstance.getBusinessKey();
            // 更新中间业务数据
            if (Objects.nonNull(busData)) {
                Object o = flowElementUtil.saveBusData(task.getProcessDefinitionId(), task.getTaskDefinitionKey(), busData);
                // 如果businessKey为空则设置
                if (StrUtil.isBlank(businessKey)) {
                    flowElementUtil.setBusinessKeyForProcessInstance(task.getProcessInstanceId(), o);
                }
            }
        } else if (StrUtil.equalsAnyIgnoreCase(approvalType, FlowApprovalType.REJECT_TO_STAR, FlowApprovalType.AGREE,
                FlowApprovalType.REFUSE, FlowApprovalType.REJECT, FlowApprovalType.AUTO_COMPLETE)) {
            if (Objects.nonNull(busData)) {
                flowElementUtil.saveBusData(task.getProcessDefinitionId(), task.getTaskDefinitionKey(), busData);
            }
            flowStateService.updateFlowState(processInstance.getProcessInstanceId(), FlowStatesEnum.IN_PROGRESS.getCode());
            // 完成任务
            FlowCompleteReqDTO flowCompleteReqDTO = new FlowCompleteReqDTO();
            flowCompleteReqDTO.setBusData(busData);
            flowCompleteReqDTO.setTaskId(taskId);
            flowCompleteReqDTO.setProcessInstanceId(processInstanceId);
            flowCompleteReqDTO.setNextNodeUserParam(comment.getNextNodeUserParam());
            flowCompleteReqDTO.setApprovalType(comment.getApprovalType());
            flowCompleteReqDTO.setComment(comment.getComment());
            flowCompleteReqDTO.setVariableData(variableData);
            commonFlowTaskCompleteService.complete(flowCompleteReqDTO);

        } else if (StrUtil.equalsAnyIgnoreCase(FlowApprovalType.CANCEL, approvalType)) {

            // 作废
            StopProcessInstanceDTO instanceDTO = new StopProcessInstanceDTO();
            instanceDTO.setProcessInstanceId(processInstanceId);
            instanceDTO.setStopReason(commentStr);
            instanceDTO.setApprovalType(approvalType);
            stopProcessInstance(instanceDTO);
            flowStateService.updateFlowState(processInstance.getProcessInstanceId(), FlowStatesEnum.CANCEL.getCode());
        } else if (StrUtil.equalsAnyIgnoreCase(FlowApprovalType.REJECT_FIRST_USER_TASK, approvalType)) {
            // 驳回到第一个用户任务
            RejectToStartDTO rejectToStartDTO = new RejectToStartDTO();
            rejectToStartDTO.setTaskId(taskId);
            rejectToStartDTO.setProcessInstanceId(processInstanceId);
            rejectToStartDTO.setBusData(busData);
            rejectToStartDTO.setReason(commentStr);
            rejectToStart(rejectToStartDTO);
            flowStateService.updateFlowState(processInstance.getProcessInstanceId(), FlowStatesEnum.RETURN.getCode());
            // 转办
        } else if (StrUtil.equalsIgnoreCase(FlowApprovalType.TRANSFER, approvalType)) {
            TurnTaskDTO param = new TurnTaskDTO();
            param.setUsername(comment.getDelegateAssignee());
            param.setTaskId(taskId);
            turnTask(param);

            // 加签
        }

        // 判断当前完成执行的任务，是否存在抄送设置
        // todo 不能在这记录数据否则存在问题 审批去重增加流程批注数据

        if (Objects.nonNull(busData)) {
            saveData(task, busData, processInstanceId, taskId, processInstance);
        }
    }

    /**
     * 构建中间变量
     * @param busData
     * @param variableData
     * @param processInstance
     */
    private void buildVariable(Map<String, Object> busData, Map<String, Object> variableData, ProcessInstance processInstance) {
        if (Objects.nonNull(busData)) {
            // 流程key
            String processDefinitionKey = processInstance.getProcessDefinitionKey();

            // 流程模板信息
            LambdaQueryWrapper<ActCustomModelInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ActCustomModelInfo::getModelKey, processDefinitionKey).last("limit 1");
            ActCustomModelInfo one = modelInfoService.getOne(queryWrapper);

            List<ActCustomVariable> list = variableService.list(new LambdaQueryWrapper<ActCustomVariable>().eq(ActCustomVariable::getModelId, one.getModelId())
                    .eq(ActCustomVariable::getVariableType, 1).eq(ActCustomVariable::getType, 0));
            list.stream().forEach(variable -> {
                String variableName = variable.getVariableName();
                variableData.put(variableName, busData.get(variableName));
            });
        }
    }

    private void saveData(Task task, Map<String, Object> busData, String processInstanceId, String taskId, ProcessInstance processInstance) {
        // 判断是否存在
        boolean exists = businessDataService.getBaseMapper().exists(new LambdaQueryWrapper<ActCustomBusinessData>()
                .eq(ActCustomBusinessData::getTaksId, taskId).eq(ActCustomBusinessData::getProcessInstanceId, processInstanceId));

        if (exists) {
            // mybatis 不支持json 数据更新；Cannot create a JSON value from a string with CHARACTER SET 'binary'.
            LambdaUpdateWrapper<ActCustomBusinessData> updateWrapper = new LambdaUpdateWrapper();
            updateWrapper.set(ActCustomBusinessData::getData, JSONObject.toJSONString(busData)).eq(ActCustomBusinessData::getTaksId, taskId)
                    .eq(ActCustomBusinessData::getProcessInstanceId, processInstanceId);
            businessDataService.update(updateWrapper);
        } else {
            // 保存每个节点的业务数据
            ActCustomBusinessData data = ActCustomBusinessData.builder()
                    .taksId(taskId)
                    .processDefinitionKey(processInstance.getProcessDefinitionKey())
                    .processDefinitionId(processInstance.getProcessDefinitionId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .data(new JSONObject(busData))
                    .taskName(task.getName())
                    .processInstanceId(processInstanceId)
                    .build();
            businessDataService.save(data);
        }
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
        if (StrUtil.isBlank(processInstanceId)) {
            log.error("流程实例Id不能为空");
            return null;
        }
        TaskQuery query = taskService.createTaskQuery().processInstanceId(processInstanceId);
        if (StrUtil.isNotBlank(taskId)) {
            query.taskId(taskId);
        }
        //
        List<Task> list = query.active().list();
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
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
        log.info("获取流程运行时指定任务的信息请求参数：processInstanceId:{}, taskId:{}",processInstanceId, taskId);
        TaskInfoDTO taskInfoDTO = new TaskInfoDTO();
        if (StrUtil.isBlank(processInstanceId) || StrUtil.isBlank(taskId)) {
            log.debug("流程实例请求参数为空！");
            return taskInfoDTO;
        }
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new AiurtBootException("该任务不存在，请联系管理员！");
        }
        String taskDefinitionKey = "";

        if (Objects.nonNull(task)) {
            taskDefinitionKey = task.getTaskDefinitionKey();
        }

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if (Objects.isNull(historicProcessInstance) || StrUtil.isBlank(taskDefinitionKey)) {
            throw new AiurtBootException(AiurtErrorEnum.PROCESS_INSTANCE_NOT_FOUND.getCode(), AiurtErrorEnum.PROCESS_INSTANCE_NOT_FOUND.getMessage());
        }

        taskInfoDTO.setBusinessKey(historicProcessInstance.getBusinessKey());

        // modelKey
        String processDefinitionKey = historicProcessInstance.getProcessDefinitionKey();
        // 查询流程模板信息
        ActCustomModelInfo actCustomModelInfo = modelInfoService.queryByModelKey(processDefinitionKey);

        if (StrUtil.isBlank(processDefinitionId) ) {
            processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        }

        // 查询流程全局配置属性
        ActCustomModelExt customModelExt = modelExtService.getByProcessDefinitionId(processDefinitionId);
        // 流程节点属性
        ActCustomTaskExt flowTaskExt =
                customTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, taskDefinitionKey);

        // 任务结束时间
        Date endTime = task.getEndTime();
        // 是否未本人的任务
        LoginUser loginUser = checkLogin();
        boolean isOwnerTask = this.isAssigneeOrCandidate(task);
        // 本人任务未结束，不显示催办，提醒功能按钮
        if (Objects.isNull(endTime) && isOwnerTask) {
            if (Objects.nonNull(flowTaskExt)) {
                if (StrUtil.isNotBlank(flowTaskExt.getOperationListJson())) {
                    List<ActOperationEntity> objectList = JSON.parseArray(flowTaskExt.getOperationListJson(), ActOperationEntity.class);
                    // 过滤，只有驳回后才能取消
                    boolean back = flowElementUtil.isBackToFirstTask(processDefinitionId, taskDefinitionKey, processInstanceId);
                    if (!back) {
                        objectList = objectList.stream().filter(entity -> !StrUtil.equalsIgnoreCase(entity.getType(), FlowApprovalType.CANCEL)).collect(Collectors.toList());
                    }

                    // 排序
                    objectList.stream().forEach(entity -> {
                        Integer o = entity.getShowOrder();
                        if (Objects.isNull(o)) {
                            entity.setShowOrder(0);
                        }
                    });
                    objectList = objectList.stream().sorted(Comparator.comparing(ActOperationEntity::getShowOrder)).collect(Collectors.toList());
                    taskInfoDTO.setOperationList(objectList);
                }

                String formJson = flowTaskExt.getFormJson();
                if (StrUtil.isNotBlank(formJson)) {
                    // 表单类型
                    JSONObject jsonObject = JSONObject.parseObject(formJson);
                    String formType = jsonObject.getString(FlowModelAttConstant.FORM_TYPE);
                    // 中间业务数据
                    // 表单设计
                    if (StrUtil.equalsIgnoreCase(formType, FlowModelAttConstant.DYNAMIC_FORM_TYPE)) {
                        setPageAttr(taskInfoDTO, jsonObject);
                    } else {
                        // 定制表单
                        taskInfoDTO.setFormType(FlowModelAttConstant.STATIC_FORM_TYPE);
                        // 判断是否是表单设计器，
                        if (StrUtil.isNotBlank(jsonObject.getString(FlowModelAttConstant.FORM_URL))) {
                            taskInfoDTO.setRouterName(jsonObject.getString(FlowModelAttConstant.FORM_URL));
                        } else {
                            taskInfoDTO.setRouterName(actCustomModelInfo.getBusinessUrl());
                        }
                    }
                } else {
                    taskInfoDTO.setRouterName(actCustomModelInfo.getBusinessUrl());
                }

                // 是否自动选人
                if (Objects.nonNull(flowTaskExt.getIsAutoSelect()) && flowTaskExt.getIsAutoSelect() == 0) {
                    taskInfoDTO.setIsAutoSelect(false);
                }
                //表单权限设置，是否可见和编辑
                JSONArray formFieldConfig = flowTaskExt.getFormFieldConfig();
                if(Objects.nonNull(formFieldConfig)){
                    taskInfoDTO.setFieldList(formFieldConfig);
                }

                // 加减签任务
                int isAddMulti = Optional.ofNullable(flowTaskExt.getIsAddMulti()).orElse(0);
                // 判断是否可以加签
                if (isAddMulti == 1) {
                    // 加签人员
                    List<String> addAssigneeVariables = taskService.getVariable(taskId, FlowVariableConstant.ADD_ASSIGNEE_LIST + taskDefinitionKey, List.class);
                    // 被加签的人员不能加签
                    if (CollUtil.isNotEmpty(addAssigneeVariables)) {
                        if (!addAssigneeVariables.contains(checkLogin().getUsername())) {
                            taskInfoDTO.setIsAddMulti(true);
                            // 判断是否可以减签,只有加签才能减签
                            Boolean multiRecordFlag = multiRecordService.existMultiRecord(checkLogin().getUsername(), task.getExecutionId());
                            if (Boolean.TRUE.equals(multiRecordFlag)) {
                                isReduceMulti(processInstanceId, taskInfoDTO, task, loginUser, addAssigneeVariables);
                            }
                        }
                    } else {
                        taskInfoDTO.setIsAddMulti(true);
                    }
                }
            }
        } else {
            String startUserId = historicProcessInstance.getStartUserId();
            // 未结束，非本人-已结束，非本人- 已结束，本人任务
            // 都是详情表单，不返回任何按钮。
            taskInfoDTO.setRouterName(actCustomModelInfo.getBusinessUrl());

            // 如果是发起人做返回催办，撤回按钮， 流程未结束, 发起节点的任务
            if (isOwnerTask && isCurrentUserInitiatorAndProcessNotEnded(startUserId, loginUser, historicProcessInstance)) {
                handleRemindLogic(taskInfoDTO, customModelExt, processInstanceId, loginUser);
            }
        }

        // 中间业务数据
        ActCustomBusinessData actCustomBusinessData = businessDataService.queryOne(processInstanceId, taskId, taskDefinitionKey);
        if (Objects.nonNull(actCustomBusinessData)) {
            taskInfoDTO.setBusData(actCustomBusinessData.getData());
        }
        if (Objects.isNull(task.getEndTime())) {
            String variableName = FlowVariableConstant.ASSIGNEE_LIST + task.getTaskDefinitionKey();
            List userNameList = taskService.getVariable(taskId, variableName, List.class);
            if (CollUtil.isNotEmpty(userNameList)) {
                taskInfoDTO.setUserName(StrUtil.join(",", userNameList));
            }
        }

        taskInfoDTO.setTaskKey(taskDefinitionKey);
        taskInfoDTO.setProcessName(historicProcessInstance.getProcessDefinitionName());
        return taskInfoDTO;
    }

    private void isReduceMulti(String processInstanceId, TaskInfoDTO taskInfoDTO, HistoricTaskInstance task, LoginUser loginUser, List<String> addAssigneeVariables) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId)
                .taskDefinitionKey(task.getTaskDefinitionKey()).list();
        // 排除自己, 正在办理中
        List<String> assigneeList = taskList.stream().filter(task1 -> !StrUtil.equalsIgnoreCase(task1.getAssignee(), loginUser.getUsername()))
                .map(Task::getAssignee).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(assigneeList)) {
            assigneeList.retainAll(addAssigneeVariables);
            if (CollUtil.isNotEmpty(assigneeList)) {
                taskInfoDTO.setIsReduceMulti(true);
            }
        }
    }


    /**
     * 用于检查是否为发起人且流程未结束
     * @param startUserId
     * @param loginUser
     * @param historicProcessInstance
     * @return
     */
    private boolean isCurrentUserInitiatorAndProcessNotEnded(String startUserId, LoginUser loginUser, HistoricProcessInstance historicProcessInstance) {
        return StrUtil.equalsIgnoreCase(startUserId, loginUser.getUsername()) && Objects.isNull(historicProcessInstance.getEndTime());
    }

    /**
     * 用于处理提醒逻辑
     * @param taskInfoDTO
     * @param customModelExt
     * @param processInstanceId
     * @param loginUser
     */
    private void handleRemindLogic(TaskInfoDTO taskInfoDTO, ActCustomModelExt customModelExt, String processInstanceId, LoginUser loginUser) {
        if (Objects.nonNull(customModelExt) && Optional.ofNullable(customModelExt.getIsRemind()).orElse(0) == 1) {
            taskInfoDTO.setIsRemind(true);
            List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
            if (CollUtil.isNotEmpty(list) && list.size() == 1) {
                Task task1 = list.get(0);
                if (Objects.nonNull(task1) && StrUtil.equalsIgnoreCase(loginUser.getUsername(), task1.getAssignee())) {
                    taskInfoDTO.setIsRemind(false);
                }
            }
        }
        //撤回按钮
        if (Objects.nonNull(customModelExt) && Optional.ofNullable(customModelExt.getIsRecall()).orElse(0) == 1) {
            taskInfoDTO.setWithdraw(true);
            //获取正在运行的节点
            List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
            //获取流程配置中的节点集合
            String recallNodeId = customModelExt.getRecallNodeId();
            if (StrUtil.isNotBlank(recallNodeId) && CollUtil.isNotEmpty(list)) {
                String[] split = recallNodeId.split(",");
                //判断节点是否在撤回集合内，不在集合内则不显示撤回按钮
                List<String> keyList =  list.stream()
                        .map(Task::getTaskDefinitionKey)
                        .filter(s -> Arrays.asList(split).contains(s))
                        .distinct()
                        .collect(Collectors.toList());
                if(CollUtil.isEmpty(keyList)){
                    taskInfoDTO.setWithdraw(false);
                }
            }
            // 获取流程实例
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (ObjectUtil.isNotEmpty(processInstance)) {
                // 获取流程定义ID
                String processDefinitionId = processInstance.getProcessDefinitionId();
                if (CollUtil.isNotEmpty(list) && list.size() == 1) {
                    UserTask userTask = flowElementUtil.getFirstUserTaskByDefinitionId(processDefinitionId);
                    String taskId = userTask.getId();
                    Task task = list.get(0);
                    String taskDefinitionKey = task.getTaskDefinitionKey();
                    // 比较当前节点与第一个用户节点是否一致，一致则不显示撤回按钮
                    boolean isStartNode = taskId.equals(taskDefinitionKey);
                    if (isStartNode) {
                        taskInfoDTO.setWithdraw(false);
                    }
                }
            }
        }
    }

    /**
     * 已办任务
     *
     * @param historicTaskReqDTO
     * @return
     */
    @Override
    public IPage<FlowHisTaskDTO> listHistoricTask(HistoricTaskReqDTO historicTaskReqDTO) {
        // 查询已办任务
        LoginUser loginUser = checkLogin();
        historicTaskReqDTO.setUserName(loginUser.getUsername());
        List<String> startTime = historicTaskReqDTO.getStartTime();
        if (CollUtil.isNotEmpty(startTime)) {
            if (startTime.size() == 1) {
                String start = startTime.get(0);
                DateTime beginDate = DateUtil.parse(start, DatePattern.NORM_DATE_PATTERN);
                DateTime dateTime = DateUtil.beginOfDay(beginDate);
                historicTaskReqDTO.setBeginDate(dateTime);

            }else if (startTime.size() == TWO_SIZE) {
                String start = startTime.get(0);
                DateTime beginDate = DateUtil.parse(start, DatePattern.NORM_DATE_PATTERN);
                DateTime dateTime = DateUtil.beginOfDay(beginDate);
                historicTaskReqDTO.setBeginDate(dateTime);

                String end = startTime.get(1);
                DateTime endDate = DateUtil.parse(end, DatePattern.NORM_DATE_PATTERN);
                DateTime endTime = DateUtil.beginOfDay(endDate);
                historicTaskReqDTO.setEndDate(endTime);
            }
        }
        Page<FlowHisTaskDTO> pageList = new Page<>(historicTaskReqDTO.getPageNo(),historicTaskReqDTO.getPageSize());
        List<FlowHisTaskDTO> hisTaskDTOList = flowApiServiceMapper.listPage(pageList, historicTaskReqDTO);

        // 封装流程定义、名称等相关信息
        if (CollUtil.isNotEmpty(hisTaskDTOList)) {

            // 封装审批类型
            Set<String> taskIdSet =
                    hisTaskDTOList.stream().map(FlowHisTaskDTO::getId).collect(Collectors.toSet());
            List<ActCustomTaskComment> commentList = customTaskCommentService.getFlowTaskCommentListByTaskIds(taskIdSet);
            Map<String, List<ActCustomTaskComment>> commentMap =
                    commentList.stream().collect(Collectors.groupingBy(ActCustomTaskComment::getTaskId));
            hisTaskDTOList.forEach(flowHisTaskDTO -> {
                List<ActCustomTaskComment> comments = commentMap.get(flowHisTaskDTO.getId());
                if (CollUtil.isNotEmpty(comments)) {
                    flowHisTaskDTO.setApprovalType(comments.get(0).getApprovalType());
                    comments.remove(0);
                }
            });
        }

        pageList.setRecords(hisTaskDTOList);
        return pageList;
    }



    /**
     * 判断当前登录用户是否为流程实例中的用户任务的指派人。或是候选人之一。
     *
     * @param task 流程实例中的用户任务。
     * @return 是返回true，否则false。
     */
    @Override
    public boolean isAssigneeOrCandidate(TaskInfo task) {
        if (Objects.isNull(task)) {
            return false;
        }
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
            query.processDefinitionKeyLike("%"+flowTaskReqDTO.getProcessDefinitionKey()+"%");
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

    @Override
    public IPage<FlowCopyDTO> listCopyInfo(FlowCopyReqDTO flowCopyReqDTO) {
        //获取当前登录的用户
        LoginUser loginUser = checkLogin();
        String userName = loginUser.getUsername();
        Page<FlowCopyDTO> page = new Page<>(flowCopyReqDTO.getPageNo(),flowCopyReqDTO.getPageSize());
        //查询所有抄送信息
        IPage<FlowCopyDTO> flowCopyPageList = actCustomProcessCopyService.queryPageList(page, flowCopyReqDTO,userName);
        return flowCopyPageList;
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
        Map<String, String> flowStateMap = flowStateService.flowStateMap(procInstanceIdSet);
        for (Task task : taskList) {
            FlowTaskDTO flowTaskVo = new FlowTaskDTO();
            flowTaskVo.setTaskId(task.getId());
            flowTaskVo.setTaskName(task.getName());
            flowTaskVo.setTaskKey(task.getTaskDefinitionKey());
            flowTaskVo.setTaskFormKey(task.getFormKey());

            ProcessDefinition processDefinition = definitionMap.get(task.getProcessDefinitionId());
            flowTaskVo.setProcessDefinitionId(processDefinition.getId());
            flowTaskVo.setProcessDefinitionName(processDefinition.getName());
            flowTaskVo.setProcessDefinitionKey(processDefinition.getKey());
            flowTaskVo.setProcessDefinitionVersion(processDefinition.getVersion());
            ProcessInstance processInstance = instanceMap.get(task.getProcessInstanceId());
            flowTaskVo.setProcessInstanceId(processInstance.getId());
            String startUserId = processInstance.getStartUserId();

            LoginUser userByName = sysBaseAPI.getUserByName(startUserId);

            flowTaskVo.setProcessInstanceInitiator(startUserId);
            if (ObjectUtil.isNotNull(userByName)){
                flowTaskVo.setProcessInstanceInitiatorName(userByName.getRealname());
            }
            flowTaskVo.setProcessInstanceStartTime(processInstance.getStartTime());
            flowTaskVo.setProcessInstanceName(processInstance.getName());
            flowTaskVo.setBusinessKey(processInstance.getBusinessKey());
            String stateName = flowStateMap.get(task.getProcessInstanceId());
            if (StrUtil.isBlank(stateName)) {
                stateName = FlowStatesEnum.IN_PROGRESS.getMessage();
            }
            flowTaskVo.setStateName(stateName);
            // 状态
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
        log.info("请求参数：{}", processInstanceId);
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
        List<HistoricActivityInstance> activityInstanceList =
                this.getHistoricActivityInstanceList(processInstanceId);
        Map<Boolean, Set<String>> partitionedTasks = activityInstanceList.stream()
                .filter(s -> ObjectUtil.isNotEmpty(s.getEndTime()))
                .sorted(Comparator.comparing(HistoricActivityInstance::getStartTime))
                .collect(Collectors.partitioningBy(
                        s -> StrUtil.equals(s.getActivityType(), "sequenceFlow"),
                        Collectors.mapping(HistoricActivityInstance::getActivityId, Collectors.toSet())
                ));
        //已完成的线路
        Set<String> finishedTaskSequenceSet = partitionedTasks.get(true);
        //已完成的任务节点
        Set<String> finishedTaskSet = partitionedTasks.get(false);

        //获取流程实例当前正在待办的节点,
        List<HistoricActivityInstance> unfinishedInstanceList =
                this.getHistoricUnfinishedInstanceList(processInstanceId);
        Set<String> unfinishedTaskSet = new LinkedHashSet<>();
        for (HistoricActivityInstance unfinishedActivity : unfinishedInstanceList) {
            unfinishedTaskSet.add(unfinishedActivity.getActivityId());
        }
        //获取用户节点办理用户
        List<HighLightedUserInfoDTO> highLightedUserInfos = getHighLightedUserInfo(processInstanceId);

        // 获取的是当前运行的xml
        byte[] bpmnXml = modelService.getBpmnXML(bpmnModel);
        String modelXml = new String(bpmnXml, StandardCharsets.UTF_8);
        HighLightedNodeDTO highLightedNodeDTO = HighLightedNodeDTO.builder()
                .finishedTaskSet(finishedTaskSet)
                .finishedSequenceFlowSet(finishedTaskSequenceSet)
                .unfinishedTaskSet(unfinishedTaskSet)
                .modelName(hpi.getProcessDefinitionName())
                .modelXml(modelXml)
                .highLightedUserInfoDTOs(highLightedUserInfos)
                .isEnd(Objects.nonNull(hpi.getEndTime()))
                .build();
        return highLightedNodeDTO;
    }

    public Map<String, HistoryTaskInfo> flowChart(String processInstanceId) {
        return flowForecastService.mergeTask(processInstanceId);
    }

    public  List<HighLightedUserInfoDTO> getHighLightedUserInfo(String processInstanceId){
        Map<String, HistoryTaskInfo> stringHistoricTaskInfoMap = flowChart(processInstanceId);
        //驳回，获取最新一次任务
        int length = 2;
        int maxNumber = 3;
        Map<String, HistoryTaskInfo> collect = stringHistoricTaskInfoMap.entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> {
                            String key = entry.getKey();
                            String[] parts = key.split("_");
                            if (parts.length >= length) {
                                return parts[0] + "_" + parts[1];
                            } else {
                                return key;
                            }
                        },
                        Collectors.maxBy(Comparator.comparing(entry -> {
                            String key = entry.getKey();
                            String[] parts = key.split("_");
                            if (parts.length >= maxNumber) {
                                return Integer.parseInt(parts[2]);
                            } else {
                                return 0;
                            }
                        }))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().map(Map.Entry::getValue).orElse(null)
                ));

        // 获取所有可能的assignee值
        List<String> allAssignees = collect.entrySet().stream()
                .map(entry -> entry.getValue().getList())
                .flatMap(list -> list.stream()
                        .filter(instance -> !"MI_END".equals(instance.getDeleteReason()))
                        .map(HistoricTaskInstance::getAssignee))
                .distinct()
                .collect(Collectors.toList());

        // 一次性查询所有assignee的信息并将其转换为映射
        List<LoginUser> allUsers = sysBaseAPI.getLoginUserList(allAssignees);
        Map<String, LoginUser> assigneeToUserMap = allUsers.stream()
                .collect(Collectors.toMap(LoginUser::getUsername, user -> user));
        //获取审核通过的用户
        List<HighLightedUserInfoDTO> highLightedUserInfos = collect.entrySet().stream()
                .map(entry -> {
                    String activityId = entry.getKey();
                    HistoryTaskInfo value = entry.getValue();
                    List<HistoricTaskInstance> list = value.getList();

                    String realNames = list.stream()
                            .filter(instance -> !"MI_END".equals(instance.getDeleteReason()))
                            .map(HistoricTaskInstance::getAssignee)
                            .distinct()
                            .map(assignee -> {
                                LoginUser user = assigneeToUserMap.get(assignee);
                                return user != null && user.getRealname() != null ? user.getRealname() : "";
                            })
                            .collect(Collectors.joining(", "));
                    HighLightedUserInfoDTO highLightedUserInfoDTO = new HighLightedUserInfoDTO();
                    highLightedUserInfoDTO.setNodeId(activityId);
                    highLightedUserInfoDTO.setRealName(realNames);
                    return highLightedUserInfoDTO;
                })
                .collect(Collectors.toList());
        return highLightedUserInfos;
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
     *
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
     * 转办任务
     *
     * @param params
     */
    @Override
    public void turnTask(TurnTaskDTO params) {
        Task task = taskService.createTaskQuery().taskId(params.getTaskId()).singleResult();
        if (ObjectUtil.isNull(task)) {
            throw new AiurtBootException("没有运行时的任务实例,请确认!");
        }
        if (StrUtil.isEmpty(params.getUsername())) {
            throw new AiurtBootException("请指定转办的人员");
        }
        // 转办
        taskService.setAssignee(params.getTaskId(), params.getUsername());
    }

    /**
     * 获取可驳回节点列表
     *
     * @param processInstanceId
     * @param taskId
     * @return
     */
    @Override
    public List<FlowNodeDTO> getBackNodesByProcessInstanceId(String processInstanceId, String taskId) {
        List<FlowNodeDTO> result = new ArrayList<>();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (ObjectUtil.isNull(task)) {
            return result;
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();

        // 获取运行节点表中usertask
        String sql = "select t.* from act_ru_actinst t where t.ACT_TYPE_ = 'userTask' " +
                " and t.PROC_INST_ID_=#{processInstanceId} and t.END_TIME_ is not null ";
        List<ActivityInstance> activityInstances = runtimeService.createNativeActivityInstanceQuery().sql(sql)
                .parameter("processInstanceId", processInstanceId)
                .list();

        // 获取运行节点表的parallelGateway节点并去重
        sql = "SELECT t.ID_, t.REV_,t.PROC_DEF_ID_,t.PROC_INST_ID_,t.EXECUTION_ID_,t.ACT_ID_, t.TASK_ID_, t.CALL_PROC_INST_ID_, t.ACT_NAME_, t.ACT_TYPE_, " +
                " t.ASSIGNEE_, t.START_TIME_, max(t.END_TIME_) as END_TIME_, t.DURATION_, t.DELETE_REASON_, t.TENANT_ID_" +
                " FROM  act_ru_actinst t WHERE t.ACT_TYPE_ = 'parallelGateway' AND t.PROC_INST_ID_ = #{processInstanceId} and t.END_TIME_ is not null" +
                " and t.ACT_ID_ <> #{actId} GROUP BY t.act_id_";
        List<ActivityInstance> parallelGatewaies = runtimeService.createNativeActivityInstanceQuery().sql(sql)
                .parameter("processInstanceId", processInstanceId)
                .parameter("actId", taskDefinitionKey)
                .list();

        // 排序
        if (CollectionUtils.isNotEmpty(parallelGatewaies)) {
            activityInstances.addAll(parallelGatewaies);
            activityInstances.sort(Comparator.comparing(ActivityInstance::getEndTime));
        }

        // 分组节点
        int count = 0;

        // 并行网关节点
        Map<ActivityInstance, List<ActivityInstance>> parallelGatewayUserTasks = new HashMap<>(16);
        List<ActivityInstance> userTasks = new ArrayList<>();
        ActivityInstance currActivityInstance = null;
        for (ActivityInstance activityInstance : activityInstances) {
            // 网关处理
            if (BpmnXMLConstants.ELEMENT_GATEWAY_PARALLEL.equals(activityInstance.getActivityType())) {
                count++;
                if (count % 2 != 0) {
                    List<ActivityInstance> datas = new ArrayList<>();
                    currActivityInstance = activityInstance;
                    parallelGatewayUserTasks.put(currActivityInstance, datas);
                }
            }
            // 用户节点
            if (BpmnXMLConstants.ELEMENT_TASK_USER.equals(activityInstance.getActivityType())) {
                if (count % 2 == 0) {
                    userTasks.add(activityInstance);
                } else {
                    if (parallelGatewayUserTasks.containsKey(currActivityInstance)) {
                        parallelGatewayUserTasks.get(currActivityInstance).add(activityInstance);
                    }
                }
            }
        }

        // 组装人员名称
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .list();

        // 获取每一步的人员信息
        if (CollectionUtils.isNotEmpty(userTasks)) {
            userTasks.forEach(activityInstance -> {
                FlowNodeDTO node = new FlowNodeDTO();
                node.setNodeId(activityInstance.getActivityId());
                node.setNodeName(activityInstance.getActivityName());
                node.setEndTime(activityInstance.getEndTime());
                result.add(node);
            });
        }

        // 组装会签节点数据
        if (CollUtil.isNotEmpty(historicTaskInstances)) {
            parallelGatewayUserTasks.forEach((activity, activities) -> {
                FlowNodeDTO node = new FlowNodeDTO();
                node.setNodeId(activity.getActivityId());
                node.setEndTime(activity.getEndTime());
                StringBuffer nodeNames = new StringBuffer("会签:");
                if (CollectionUtils.isNotEmpty(activities)) {
                    activities.forEach(activityInstance -> {
                        nodeNames.append(activityInstance.getActivityName()).append(",");
                    });
                    node.setNodeName(nodeNames.toString());
                    result.add(node);
                }
            });
        }

        // 去重合并
        List<FlowNodeDTO> datas = result.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(nodeVo -> nodeVo.getNodeId()))), ArrayList::new));

        // 排序
        datas.sort(Comparator.comparing(FlowNodeDTO::getEndTime));
        return datas;
    }

    /**
     * 获取每一步的人员信息
     *
     * @param processInstanceId
     * @param userList
     * @param taskInstanceMap
     * @return map
     */
    private Map<String, String> getApplyers(String processInstanceId, List<String> userList, Map<String, List<HistoricTaskInstance>> taskInstanceMap) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        return null;
    }

    /**
     * 流程实例
     *
     * @param reqDTO
     * @return
     */
    @Override
    public IPage<HistoricProcessInstanceDTO> listAllHistoricProcessInstance(HistoricProcessInstanceReqDTO reqDTO) {

        Page<HistoricProcessInstanceDTO> pages = new Page<>(reqDTO.getPageNo(), reqDTO.getPageSize());

        if (Objects.nonNull(reqDTO.getStartTime())) {
            DateTime dateTime = DateUtil.beginOfDay(reqDTO.getStartTime());
            reqDTO.setStartTime(dateTime);
        }

        if (Objects.nonNull(reqDTO.getEndTime())) {
            DateTime dateTime = DateUtil.endOfDay(reqDTO.getEndTime());
            reqDTO.setEndTime(dateTime);
        }

        if (StrUtil.isNotBlank(reqDTO.getLoginName())) {
            List<String> userNameList = flowUserService.getUserName(reqDTO.getLoginName());
            reqDTO.setUserNameList(userNameList);
        }

        List<HistoricProcessInstanceDTO> dtoList = flowApiServiceMapper.listPageHistoricProcessInstance(pages, reqDTO);

        dtoList = Optional.ofNullable(dtoList).orElse(Collections.emptyList());

        List<String> userNameList = dtoList.stream().map(HistoricProcessInstanceDTO::getUserName).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(userNameList)) {
            List<LoginUser> loginUserList = sysBaseAPI.getLoginUserList(userNameList);
            Map<String, String> userMap = loginUserList.stream().collect(Collectors.toMap(LoginUser::getUsername, LoginUser::getRealname, (t1, t2) -> t1));
            dtoList.stream().forEach(historicProcessInstanceDTO -> historicProcessInstanceDTO.setRealName(userMap.get(historicProcessInstanceDTO.getUserName())));
        }

        dtoList.stream().forEach(historicProcessInstanceDTO -> {
            if (Objects.nonNull(historicProcessInstanceDTO.getState())) {
                FlowStatesEnum statesEnum = FlowStatesEnum.getByCode(historicProcessInstanceDTO.getState());
                if (Objects.nonNull(statesEnum)) {
                    historicProcessInstanceDTO.setStateName(statesEnum.getMessage());
                }else {
                    historicProcessInstanceDTO.setStateName(Objects.isNull(historicProcessInstanceDTO.getEndTime()) ?
                            FlowStatesEnum.IN_PROGRESS.getMessage() : FlowStatesEnum.COMPLETE.getMessage());
                }
            }else {
                historicProcessInstanceDTO.setStateName(Objects.isNull(historicProcessInstanceDTO.getEndTime()) ?
                        FlowStatesEnum.IN_PROGRESS.getMessage() : FlowStatesEnum.COMPLETE.getMessage());
            }
        });


        pages.setRecords(dtoList);
        return pages;
    }


    /**
     * 终止流程
     *
     * @param instanceDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stopProcessInstance(StopProcessInstanceDTO instanceDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceDTO.getProcessInstanceId()).singleResult();

        if (Objects.isNull(historicProcessInstance)) {
            throw new AiurtBootException("该流程实例不存在！");
        }
        Date endTime = historicProcessInstance.getEndTime();
        if (Objects.nonNull(endTime)) {
            throw new AiurtBootException("该流程实例结束！");
        }

        String definitionId = historicProcessInstance.getProcessDefinitionId();
        // The process instance id (== as the id for the runtime process instance).
        String processInstanceId = historicProcessInstance.getId();
        List<Task> list = taskService.createTaskQuery().processInstanceId(instanceDTO.getProcessInstanceId()).active().list();

        if (CollUtil.isEmpty(list)) {
            throw new AiurtBootException("当前流程尚未开始或已经结束！");
        }

        EndEvent endEvent = flowElementUtil.getEndEvent(definitionId);

        if (Objects.isNull(endEvent)) {
            throw new AiurtBootException("配置错误，缺少结束节点, 请联系管理员");
        }


        List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstanceId).list();
        List<String> executionIds = executions.stream().map(Execution::getId).collect(Collectors.toList());


        boolean hasVariable = runtimeService.hasVariable(processInstanceId, FlowModelAttConstant.CANCEL);
        if (!hasVariable) {
            runtimeService.setVariable(processInstanceId, FlowModelAttConstant.CANCEL, true);
        } else {
            runtimeService.setVariable(processInstanceId, FlowModelAttConstant.CANCEL, false);
        }

        Map<String, Object> localVariables = new HashMap<>(16);
        localVariables.put(FlowConstant.STOP_PROCESS, Boolean.TRUE);
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(instanceDTO.getProcessInstanceId())
                .moveExecutionsToSingleActivityId(executionIds, endEvent.getId())
                .localVariables(endEvent.getId(), localVariables)
                .changeState();

        List<ActCustomTaskComment> taskCommentList = new ArrayList<>();
        for (Task task : list) {
            // 添加审批意见,用户不可见
            ActCustomTaskComment actCustomTaskComment = new ActCustomTaskComment(task);
            actCustomTaskComment.setApprovalType(instanceDTO.getApprovalType());
            actCustomTaskComment.setCreateRealname(loginUser.getRealname());
            actCustomTaskComment.setIsVisible(0);
            actCustomTaskComment.setComment(instanceDTO.getStopReason());
            taskCommentList.add(actCustomTaskComment);
            // 更新待办
            todoBaseApi.updateBpmnTaskState(task.getId(), processInstanceId, loginUser.getUsername(), "1");
        }

        // 作废，
        ActCustomTaskComment actCustomTaskComment = new ActCustomTaskComment();
        actCustomTaskComment.setProcessInstanceId(processInstanceId);
        actCustomTaskComment.setApprovalType(instanceDTO.getApprovalType());
        if (StrUtil.equalsIgnoreCase(instanceDTO.getApprovalType(), FlowApprovalType.CANCEL)) {
            actCustomTaskComment.setTaskId(list.get(0).getId());
            actCustomTaskComment.setTaskKey(list.get(0).getTaskDefinitionKey());
            actCustomTaskComment.setTaskName(list.get(0).getName());
        }
        actCustomTaskComment.setCreateRealname(loginUser.getRealname());
        actCustomTaskComment.setComment(instanceDTO.getStopReason());
        taskCommentList.add(actCustomTaskComment);
        customTaskCommentService.saveBatch(taskCommentList);

        if (StrUtil.equalsIgnoreCase(FlowApprovalType.STOP, instanceDTO.getApprovalType())) {
            flowStateService.updateFlowState(processInstanceId, FlowStatesEnum.TERMINATED.getCode());
        }
        // 发送redis事件
    }

    /**
     * 驳回到第一个用户任务
     */
    public void rejectToStart(RejectToStartDTO instanceDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String processInstanceId = instanceDTO.getProcessInstanceId();

        ProcessInstance processInstance = getProcessInstance(processInstanceId);

        if (Objects.isNull(processInstance)) {
            throw new AiurtBootException("流程实例不存在！请刷新。");
        }

        UserTask firstUserTask = flowElementUtil.getFirstUserTaskByDefinitionId(processInstance.getProcessDefinitionId());

        List<Task> taskList = taskService.createTaskQuery().processInstanceId(instanceDTO.getProcessInstanceId()).list();

        List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstanceId).list();
        List<String> executionIds = executions.stream().map(Execution::getId).collect(Collectors.toList());

        Map<String, Object> localVariableMap = new HashMap<>(16);
        localVariableMap.put(BackNodeRuleVerifyHandler.REJECT_FIRST_USER_TASK, true);
        // 流程跳转, flowable 已提供, 存在分支会存在问题
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(instanceDTO.getProcessInstanceId())
                .moveExecutionsToSingleActivityId(executionIds, firstUserTask.getId())
                .localVariables(firstUserTask.getId(), localVariableMap)
                .changeState();

        // 增加不可见的日志，，记录日志
        List<ActCustomTaskComment> taskCommentList = taskList.stream().map(task -> {
            ActCustomTaskComment actCustomTaskComment = new ActCustomTaskComment();
            actCustomTaskComment.setTaskId(task.getId());
            actCustomTaskComment.setTaskKey(task.getTaskDefinitionKey());
            actCustomTaskComment.setTaskName(task.getName());
            actCustomTaskComment.setCreateRealname(loginUser.getRealname());
            actCustomTaskComment.setComment(instanceDTO.getReason());
            actCustomTaskComment.setApprovalType(FlowApprovalType.REJECT_FIRST_USER_TASK);
            actCustomTaskComment.setProcessInstanceId(processInstanceId);
            actCustomTaskComment.setIsVisible(0);
            return actCustomTaskComment;
        }).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(taskCommentList)) {
            taskCommentService.saveBatch(taskCommentList);
        }
    }

    /**
     * 删除流程
     *
     * @param processInstanceId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProcessInstance(String processInstanceId, String delReason) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 查询流程实例和历史流程实例
        ProcessInstance processInstance = getProcessInstance(processInstanceId);
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (Objects.isNull(processInstance) && Objects.isNull(historicProcessInstance)) {
            throw new AiurtBootException("该流程实例已被删除！");
        }

        if (ObjectUtil.isNotEmpty(processInstance)) {
            runtimeService.deleteProcessInstance(processInstanceId, delReason);
            // 操作日志
            ActCustomTaskComment actCustomTaskComment = new ActCustomTaskComment();
            actCustomTaskComment.setProcessInstanceId(processInstanceId);
            actCustomTaskComment.setComment(delReason);
            actCustomTaskComment.setApprovalType(FlowApprovalType.DELETE);
            actCustomTaskComment.setCreateRealname(loginUser.getUsername());
            customTaskCommentService.getBaseMapper().insert(actCustomTaskComment);
        } else {
            historyService.deleteHistoricProcessInstance(processInstanceId);
        }

        //todo 工单删除
    }

    /**
     * 回退到上一个用户任务节点。如果没有指定，则回退到上一个任务。
     *
     * @param task      当前活动任务。
     * @param targetKey 指定回退到的任务标识。如果为null，则回退到上一个任务。
     * @param forReject true表示驳回，false为撤回。
     * @param comment   驳回或者撤销的原因。
     */
    @Override
    public void backToRuntimeTask(Task task, String targetKey, boolean forReject, String comment) {
        // 验证并获取流程的实时任务信息。
        this.verifyAndGetRuntimeTaskInfo(task);

        // 判断当前登录用户是否为流程实例中的用户任务的指派人。或是候选人之一，如果是候选人则拾取该任务并成为指派人。
        // 如果都不是，就会返回具体的错误信息。
        this.verifyAssigneeOrCandidateAndClaim(task);

        ProcessDefinition processDefinition = this.getProcessDefinitionById(task.getProcessDefinitionId());

        // 全部流程节点
        Collection<FlowElement> allElements = this.getProcessAllElements(processDefinition.getId());
        FlowElement source = null;
        // 获取跳转的节点元素
        FlowElement target = null;
        for (FlowElement flowElement : allElements) {
            if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                source = flowElement;
                if (StrUtil.isBlank(targetKey)) {
                    break;
                }
            }
            if (StrUtil.isNotBlank(targetKey)) {
                if (flowElement.getId().equals(targetKey)) {
                    target = flowElement;
                }
            }
        }
        if (targetKey != null && target == null) {
            throw new AiurtBootException("数据验证失败，被驳回的指定目标节点不存在！");
        }

        UserTask oneUserTask = null;
        List<String> targetIds = null;

        // 没有指定跳到哪一步的情况，则跳回当前任务的上一步
        if (target == null) {
            List<UserTask> parentUserTaskList =
                    this.getParentUserTaskList(source, null, null);
            if (CollUtil.isEmpty(parentUserTaskList)) {
                throw new AiurtBootException("数据验证失败，当前节点为初始任务节点，不能驳回！");
            }

            // 获取活动ID, 即节点Key
            Set<String> parentUserTaskKeySet = new HashSet<>();
            parentUserTaskList.forEach(item -> parentUserTaskKeySet.add(item.getId()));
            List<HistoricActivityInstance> historicActivityIdList =
                    this.getHistoricActivityInstanceListOrderByStartTime(task.getProcessInstanceId());

            // 数据清洗，将回滚导致的脏数据清洗掉
            List<String> lastHistoricTaskInstanceList =
                    this.cleanHistoricTaskInstance(allElements, historicActivityIdList);

            // 此时历史任务实例为倒序，获取最后走的节点
            targetIds = new ArrayList<>();
            // 循环结束标识，遇到当前目标节点的次数
            int number = 0;
            StringBuilder parentHistoricTaskKey = new StringBuilder();
            for (String historicTaskInstanceKey : lastHistoricTaskInstanceList) {
                // 当会签时候会出现特殊的，连续都是同一个节点历史数据的情况，这种时候跳过
                if (parentHistoricTaskKey.toString().equals(historicTaskInstanceKey)) {
                    continue;
                }
                parentHistoricTaskKey = new StringBuilder(historicTaskInstanceKey);
                if (historicTaskInstanceKey.equals(task.getTaskDefinitionKey())) {
                    number++;
                }
                if (number == 2) {
                    break;
                }
                // 如果当前历史节点，属于父级的节点，说明最后一次经过了这个点，需要退回这个点
                if (parentUserTaskKeySet.contains(historicTaskInstanceKey)) {
                    targetIds.add(historicTaskInstanceKey);
                }
            }
            // 目的获取所有需要被跳转的节点 currentIds
            // 取其中一个父级任务，因为后续要么存在公共网关，要么就是串行公共线路
            oneUserTask = parentUserTaskList.get(0);
        }
        // 获取所有正常进行的执行任务的活动节点ID，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Execution> runExecutionList =
                runtimeService.createExecutionQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runActivityIdList = runExecutionList.stream()
                .filter(c -> StrUtil.isNotBlank(c.getActivityId()))
                .map(Execution::getActivityId).collect(Collectors.toList());
        // 需驳回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runExecutionList 比对，获取需要撤回的任务
        List<FlowElement> currentFlowElementList = this.getChildUserTaskList(
                target != null ? target : oneUserTask, runActivityIdList, null, null);
        currentFlowElementList.forEach(item -> currentIds.add(item.getId()));
        if (target == null) {
            // 规定：并行网关之前节点必须需存在唯一用户任务节点，如果出现多个任务节点，则并行网关节点默认为结束节点，原因为不考虑多对多情况
            if (targetIds.size() > 1 && currentIds.size() > 1) {
                throw new AiurtBootException("数据验证失败，任务出现多对多情况，无法撤回！");
            }
        }
        AtomicReference<List<HistoricActivityInstance>> tmp = new AtomicReference<>();
        // 用于下面新增网关删除信息时使用
        String targetTmp = targetKey != null ? targetKey : String.join(",", targetIds);
        // currentIds 为活动ID列表
        // currentExecutionIds 为执行任务ID列表
        // 需要通过执行任务ID来设置驳回信息，活动ID不行
        currentIds.forEach(currentId -> runExecutionList.forEach(runExecution -> {
            if (StrUtil.isNotBlank(runExecution.getActivityId()) && currentId.equals(runExecution.getActivityId())) {
                // 查询当前节点的执行任务的历史数据
                tmp.set(historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(task.getProcessInstanceId())
                        .executionId(runExecution.getId())
                        .activityId(runExecution.getActivityId()).list());
                // 如果这个列表的数据只有 1 条数据
                // 网关肯定只有一条，且为包容网关或并行网关
                // 这里的操作目的是为了给网关在扭转前提前加上删除信息，结构与普通节点的删除信息一样，目的是为了知道这个网关也是有经过跳转的
                if (tmp.get() != null && tmp.get().size() == 1 && StrUtil.isNotBlank(tmp.get().get(0).getActivityType())
                        && ("parallelGateway".equals(tmp.get().get(0).getActivityType()) || "inclusiveGateway".equals(tmp.get().get(0).getActivityType()))) {
                    // singleResult 能够执行更新操作
                    // 利用 流程实例ID + 执行任务ID + 活动节点ID 来指定唯一数据，保证数据正确
                    historyService.createNativeHistoricActivityInstanceQuery().sql(
                            "UPDATE ACT_HI_ACTINST SET DELETE_REASON_ = 'Change activity to " + targetTmp + "'  WHERE PROC_INST_ID_='" + task.getProcessInstanceId() + "' AND EXECUTION_ID_='" + runExecution.getId() + "' AND ACT_ID_='" + runExecution.getActivityId() + "'").singleResult();
                }
            }
        }));
        try {
            if (StrUtil.isNotBlank(targetKey)) {
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(task.getProcessInstanceId())
                        .moveActivityIdsToSingleActivityId(currentIds, targetKey).changeState();
            } else {
                // 如果父级任务多于 1 个，说明当前节点不是并行节点，原因为不考虑多对多情况
                if (targetIds.size() > 1) {
                    // 1 对 多任务跳转，currentIds 当前节点(1)，targetIds 跳转到的节点(多)
                    ChangeActivityStateBuilder builder = runtimeService.createChangeActivityStateBuilder()
                            .processInstanceId(task.getProcessInstanceId())
                            .moveSingleActivityIdToActivityIds(currentIds.get(0), targetIds);
                    for (String targetId : targetIds) {
                        ActCustomTaskComment taskComment =
                                customTaskCommentService.getLatestFlowTaskComment(task.getProcessInstanceId(), targetId);
                        // 如果驳回后的目标任务包含指定人，则直接通过变量回抄，如果没有则自动忽略该变量，不会给流程带来任何影响。
                        String submitLoginName = taskComment.getCreateBy();
                        if (StrUtil.isNotBlank(submitLoginName)) {
                            builder.localVariable(targetId, FlowConstant.TASK_APPOINTED_ASSIGNEE_VAR, submitLoginName);
                        }
                    }
                    builder.changeState();
                }
                // 如果父级任务只有一个，因此当前任务可能为网关中的任务
                if (targetIds.size() == 1) {
                    // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetIds.get(0) 跳转到的节点(1)
                    // 如果驳回后的目标任务包含指定人，则直接通过变量回抄，如果没有则自动忽略该变量，不会给流程带来任何影响。
                    ChangeActivityStateBuilder builder = runtimeService.createChangeActivityStateBuilder()
                            .processInstanceId(task.getProcessInstanceId())
                            .moveActivityIdsToSingleActivityId(currentIds, targetIds.get(0));
                    ActCustomTaskComment taskComment =
                            customTaskCommentService.getLatestFlowTaskComment(task.getProcessInstanceId(), targetIds.get(0));
                    String submitLoginName = taskComment.getCreateBy();
                    if (StrUtil.isNotBlank(submitLoginName)) {
                        builder.localVariable(targetIds.get(0), FlowConstant.TASK_APPOINTED_ASSIGNEE_VAR, submitLoginName);
                    }
                    builder.changeState();
                }
            }
            ActCustomTaskComment customTaskComment = new ActCustomTaskComment();
            customTaskComment.setTaskId(task.getId());
            customTaskComment.setTaskKey(task.getTaskDefinitionKey());
            customTaskComment.setTaskName(task.getName());
            customTaskComment.setApprovalType(forReject ? "reject" : "revoke");
            customTaskComment.setProcessInstanceId(task.getProcessInstanceId());
            customTaskComment.setComment(comment);
            customTaskCommentService.getBaseMapper().insert(customTaskComment);
        } catch (Exception e) {
            log.error("Failed to execute moveSingleActivityIdToActivityIds", e);
            throw new AiurtBootException(e.getMessage());
        }
    }

    private void verifyAssigneeOrCandidateAndClaim(Task task) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(loginUser)) {
            throw new AiurtBootException("请重新登录！");
        }
        // 这里必须先执行拾取操作，如果当前用户是候选人，特别是对于分布式场景，更是要先完成候选人的拾取。
        if (task.getAssignee() == null) {
            // 没有指派人
            if (!this.isAssigneeOrCandidate(task)) {
                throw new AiurtBootException("数据验证失败，当前用户不是该待办任务的候选人，请刷新后重试！");
            }
            // 作为候选人主动拾取任务。
            taskService.claim(task.getId(), loginUser.getUsername());
        } else {
            if (!task.getAssignee().equals(loginUser.getUsername())) {
                throw new AiurtBootException("数据验证失败，当前用户不是该待办任务的指派人，请刷新后重试！");
            }
        }
    }

    /**
     * 根据流程定义Id查询流程定义对象。
     *
     * @param processDefinitionId 流程定义Id。
     * @return 流程定义对象。
     */
    @Override
    public ProcessDefinition getProcessDefinitionById(String processDefinitionId) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
    }

    /**
     * 获取指定流程定义的全部流程节点。
     *
     * @param processDefinitionId 流程定义Id。
     * @return 当前流程定义的全部节点集合。
     */
    @Override
    public Collection<FlowElement> getProcessAllElements(String processDefinitionId) {
        Process process = repositoryService.getBpmnModel(processDefinitionId).getProcesses().get(0);
        return this.getAllElements(process.getFlowElements(), null);
    }

    /**
     * 获取流程实例的已完成历史任务列表，同时按照每个活动实例的开始时间升序排序。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例已完成的历史任务列表。
     */
    @Override
    public List<HistoricActivityInstance> getHistoricActivityInstanceListOrderByStartTime(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
    }

    /**
     * 获取开始节点之后的第一个任务节点的数据。
     *
     * @param processDefinitionKey 流程标识。
     * @return 任务节点的自定义对象数据。
     */
    @Override
    public TaskInfoDTO viewInitialTaskInfo(String processDefinitionKey) {

        ProcessDefinition processDefinition = flowElementUtil.getProcessDefinition(processDefinitionKey);
        if (processDefinition.isSuspended()) {
            throw new AiurtBootException("当前程主版本已被挂起，请联系管理员！");
        }

        UserTask userTask = flowElementUtil.getFirstUserTaskByModelKey(processDefinitionKey);

        // 下
        if (Objects.isNull(userTask)) {
            throw new AiurtBootException(AiurtErrorEnum.FLOW_TASK_NOT_FOUND.getCode(), AiurtErrorEnum.FLOW_TASK_NOT_FOUND.getMessage());
        }
        TaskInfoDTO taskInfoDTO = new TaskInfoDTO();
        taskInfoDTO.setTaskKey(userTask.getId());

        ActCustomTaskExt customTaskExt = customTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinition.getId(), userTask.getId());
        if (Objects.nonNull(customTaskExt)) {
            String formJson = customTaskExt.getFormJson();
            if (StrUtil.isNotBlank(formJson)) {
                JSONObject jsonObject = JSONObject.parseObject(formJson);
                // 表单类型
                String formType = jsonObject.getString(FlowModelAttConstant.FORM_TYPE);
                // 表单设计
                if (StrUtil.equalsIgnoreCase(formType, FlowModelAttConstant.DYNAMIC_FORM_TYPE)) {
                    setPageAttr(taskInfoDTO, jsonObject);
                } else {
                    // 定制表单
                    taskInfoDTO.setFormType(FlowModelAttConstant.STATIC_FORM_TYPE);
                    // 判断是否是表单设计器，
                    taskInfoDTO.setRouterName(jsonObject.getString(FlowModelAttConstant.FORM_URL));
                }
            }

            String json = customTaskExt.getOperationListJson();
            if (StrUtil.isNotBlank(json)) {
                List<ActOperationEntity> objectList = JSONObject.parseArray(json, ActOperationEntity.class);
                // 过滤 取消按钮不展示
                objectList = objectList.stream().filter(entity -> !StrUtil.equalsIgnoreCase(entity.getType(), FlowApprovalType.CANCEL)).collect(Collectors.toList());
                // 排序
                objectList.stream().forEach(entity -> {
                    Integer o = entity.getShowOrder();
                    if (Objects.isNull(o)) {
                        entity.setShowOrder(0);
                    }
                });
                objectList = objectList.stream().sorted(Comparator.comparing(ActOperationEntity::getShowOrder)).collect(Collectors.toList());

                taskInfoDTO.setOperationList(objectList);

                if (Objects.nonNull(customTaskExt.getIsAutoSelect()) && customTaskExt.getIsAutoSelect() == 0) {
                    taskInfoDTO.setIsAutoSelect(false);
                }
            }
        }
        taskInfoDTO.setProcessName(processDefinition.getName());
        taskInfoDTO.setProcessDefinitionKey(processDefinitionKey);
        //获取表单权限设置
        JSONArray formFieldConfig = customTaskExt.getFormFieldConfig();
        if(Objects.nonNull(formFieldConfig)){
            taskInfoDTO.setFieldList(formFieldConfig);
        }
        return taskInfoDTO;
    }

    private void setPageAttr(TaskInfoDTO taskInfoDTO, JSONObject jsonObject) {
        String formDynamicUrl = jsonObject.getString(FlowModelAttConstant.FORM_DYNAMIC_URL);
        if (StrUtil.isNotBlank(formDynamicUrl)) {
            ActCustomPage customPage = pageService.getById(formDynamicUrl);
            if (Objects.nonNull(customPage)) {
                taskInfoDTO.setPageId(formDynamicUrl);
                taskInfoDTO.setPageContentJson(customPage.getPageContentJson());
                taskInfoDTO.setPageJSon(customPage.getPageJson());
            }

        }
        taskInfoDTO.setFormType(FlowModelAttConstant.DYNAMIC_FORM_TYPE);
    }


    /**
     * 根据业务数据获取历史活动
     *
     * @param businessKey
     * @return
     */
    @Override
    public List<HistoricTaskInfo> getHistoricLog(String businessKey) {

        if (Objects.isNull(businessKey)) {
            return Collections.emptyList();
        }

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();

        if (Objects.isNull(historicProcessInstance)) {
            return Collections.emptyList();
        }

        List<HistoricTaskInfo> historicTaskInfoList = buildHistoricTaskInfo(historicProcessInstance);
        return historicTaskInfoList;
    }

    @NotNull
    private List<HistoricTaskInfo> buildHistoricTaskInfo(HistoricProcessInstance processInstance) {
        List<HistoricTaskInstance> instanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstance.getId()).orderByHistoricTaskInstanceStartTime().desc().list();
        instanceList = instanceList.stream().filter(historicTaskInstance -> !StrUtil.equalsIgnoreCase("_AUTO_COMPLETE", historicTaskInstance.getAssignee())).collect(Collectors.toList());
        // 需要重构， 已办理的，未办理的， 已办理的需要
        List<HistoricTaskInfo> historicTaskInfoList = new ArrayList<>();
        HistoricTaskInstance historicTaskInstance = instanceList.get(0);
        String firstTaskKey = null;
        if (Objects.nonNull(historicTaskInstance)) {
            String processDefinitionId = historicTaskInstance.getProcessDefinitionId();
            UserTask userTask = flowElementUtil.getFirstUserTaskByDefinitionId(processDefinitionId);
            firstTaskKey = userTask.getId();
        }
        String finalFirstTaskKey = firstTaskKey;
        // 已完成的，
        List<HistoricTaskInstance> finishList = instanceList.stream().filter(taskInstance -> Objects.nonNull(taskInstance.getEndTime()))
                .filter(taskInstance -> StrUtil.equalsIgnoreCase(finalFirstTaskKey, taskInstance.getTaskDefinitionKey()) || Objects.nonNull(taskInstance.getClaimTime())).collect(Collectors.toList());

        // 未完成的
        List<HistoricTaskInstance> unFinishList = instanceList.stream().filter(taskInstance -> Objects.isNull(taskInstance.getEndTime())).collect(Collectors.toList());

        //
        String[] userNameList = finishList.stream().map(HistoricTaskInstance::getAssignee).toArray(String[]::new);

        List<LoginUser> loginUserList = sysBaseAPI.queryUserByNames(userNameList);
        Map<String, LoginUser> userMap = loginUserList.stream().collect(Collectors.toMap(LoginUser::getUsername, t -> t, (t1, t2) -> t1));

        Map<String, List<HistoricTaskInstance>> unFinishMap = unFinishList.stream().collect(Collectors.groupingBy(HistoricTaskInstance::getTaskDefinitionKey));

        unFinishMap.forEach((key,list)->{
            List<HistoricTaskInstance> taskInstanceList = unFinishMap.get(key);
            HistoricTaskInstance entity = taskInstanceList.get(0);
            HistoricTaskInfo historicTaskInfo = bulidHistorcTaskInfo(entity);
            if (taskInstanceList.size() == 0) {
                String assignee = entity.getAssignee();
                if (StrUtil.isBlank(assignee)) {
                    List<IdentityLink> links = taskService.getIdentityLinksForTask(entity.getId());
                    List<String> nameList = links.stream().map(IdentityLink::getUserId).collect(Collectors.toList());
                    setAssign(historicTaskInfo, nameList);
                }else {
                    setAssign(historicTaskInfo, Collections.singletonList(assignee));
                }
            }else {
                List<String> nameList = taskInstanceList.stream().map(HistoricTaskInstance::getAssignee).collect(Collectors.toList());
                setAssign(historicTaskInfo, nameList);
            }
            historicTaskInfoList.add(historicTaskInfo);
        });

        finishList.stream().forEach(entity -> {
            HistoricTaskInfo historicTaskInfo = bulidHistorcTaskInfo(entity);
            String executionId = entity.getExecutionId();

            LoginUser userByName = null;
            if (StrUtil.isNotBlank(entity.getAssignee())) {
                userByName = userMap.get(entity.getAssignee());
            }

            if (Objects.nonNull(userByName)) {
                historicTaskInfo.setAssigne(userByName.getRealname() + "(所属部门-" + userByName.getOrgName() + ")");
                historicTaskInfo.setAssignName(userByName.getUsername());
            }
            // // 查询审批意见 且判断第一个节点
            if (!StrUtil.equalsIgnoreCase(entity.getTaskDefinitionKey(), finalFirstTaskKey)) {
                ActCustomTaskComment actCustomTaskComment = actCustomTaskCommentMapper.selectOne(new LambdaQueryWrapper<ActCustomTaskComment>().eq(ActCustomTaskComment::getTaskId, entity.getId()).orderByDesc(ActCustomTaskComment::getCreateTime).last("limit 1"));
                if (Objects.nonNull(actCustomTaskComment)) {
                    historicTaskInfo.setResult(FlowApprovalType.DICT_MAP.get(actCustomTaskComment.getApprovalType()));
                    historicTaskInfo.setRemark(actCustomTaskComment.getComment());
                }
            }
            historicTaskInfoList.add(historicTaskInfo);
        });

        return historicTaskInfoList;
    }

    /**
     * 设置用户名
     * @param historicTaskInfo
     * @param nameList
     */
    private void setAssign(HistoricTaskInfo historicTaskInfo, List<String> nameList) {
        List<LoginUser> userListByName = sysBaseAPI.getLoginUserList(nameList);

        if (CollectionUtil.isNotEmpty(userListByName)) {
            historicTaskInfo.setAssignName(StrUtil.join(",", userListByName.stream().map(LoginUser::getUsername).collect(Collectors.toList())));
            List<String> collect = userListByName.stream().map(user -> user.getRealname() + "(所属部门-" + user.getOrgName() + ")").collect(Collectors.toList());
            historicTaskInfo.setAssigne(StrUtil.join(",", collect));
        }
    }

    /**
     * 构建HistorcTaskInfo
     * @param entity
     * @return
     */
    private HistoricTaskInfo bulidHistorcTaskInfo(HistoricTaskInstance entity) {
        HistoricTaskInfo historicTaskInfo = HistoricTaskInfo.builder()
                .id(entity.getId())
                .createTime(DateUtil.format(entity.getCreateTime(), "yyyy-MM-dd HH:mm:ss"))
                .endTime(DateUtil.format(entity.getEndTime(), "yyyy-MM-dd HH:mm:ss"))
                .taskName(entity.getName())
                .state(Objects.isNull(entity.getEndTime()) ? "未完成" : "已完成")
                .taskId(entity.getId())
                .build();
        if (Objects.nonNull(entity.getEndTime())) {
            historicTaskInfo.setCostTime(DateUtil.formatBetween(entity.getCreateTime(), entity.getEndTime(), BetweenFormater.Level.SECOND));
        }
        return historicTaskInfo;
    }

    /**
     * 根据ProcessInstanceId 获取历史记录
     *
     * @param processInstanceId
     * @return
     */
    @Override
    public List<HistoricTaskInfo> getHistoricLogByProcessInstanceId(String processInstanceId) {

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if (Objects.isNull(historicProcessInstance)) {
            return Collections.emptyList();
        }

        List<HistoricTaskInfo> historicTaskInfoList = buildHistoricTaskInfo(historicProcessInstance);

        return historicTaskInfoList;

    }

    /**
     * 根据ProcessInstanceId 获取流程实例状态
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @Override
    public ProcessInstanceStateResult getProcessInstanceState(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if (Objects.isNull(historicProcessInstance)) {
            throw new AiurtBootException("不存在该流程记录");
        }

        Date endTime = historicProcessInstance.getEndTime();

        ProcessInstanceStateResult result = new ProcessInstanceStateResult();
        result.setProcessStates(0);
        if (Objects.nonNull(endTime)) {
            result.setProcessStates(1);
        }
        result.setProcessInstanceId(processInstanceId);

        return result;
    }

    /**
     * 任务已结束的流程表单信息
     *
     * @param processInstanceId
     * @return
     */
    @Override
    public TaskInfoDTO viewEndProcessTaskInfo(String processInstanceId) {
        TaskInfoDTO taskInfoDTO = new TaskInfoDTO();
        // 流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if (Objects.isNull(historicProcessInstance)) {
            throw new AiurtBootException("不存在该流程记录");
        }

        String definitionId = historicProcessInstance.getProcessDefinitionId();

        // 流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(definitionId).singleResult();

        // 流程模板信息
        LambdaQueryWrapper<ActCustomModelInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomModelInfo::getModelKey, processDefinition.getKey()).last("limit 1");
        ActCustomModelInfo one = modelInfoService.getOne(queryWrapper);

        // 默认的首页地址

        // 业务数据
        ActCustomBusinessData actCustomBusinessData = businessDataService.queryByProcessInstanceId(processInstanceId, null);

        taskInfoDTO.setFormType(FlowModelAttConstant.STATIC_FORM_TYPE);
        // 判断是否是表单设计器，
        taskInfoDTO.setRouterName(one.getBusinessUrl());

        taskInfoDTO.setBusData(actCustomBusinessData.getData());
        return taskInfoDTO;
    }

    @Override
    public List<ProcessParticipantsInfoDTO> getProcessParticipantsInfo(ProcessParticipantsReqDTO processParticipantsReqDTO) {
        List<ProcessParticipantsInfoDTO> result = CollUtil.newArrayList();

        Task task = this.getProcessInstanceActiveTask(processParticipantsReqDTO.getProcessInstanceId(), processParticipantsReqDTO.getTaskId());
        if (task == null) {
            throw new AiurtBootException("数据验证失败，请核对指定的任务Id，请刷新后重试！");
        }

        ProcessInstance processInstance = this.getProcessInstance(task.getProcessInstanceId());

        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String taskDefinitionKey = task.getTaskDefinitionKey();

        String processDefinitionId = task.getProcessDefinitionId();

        // 是否自动选人
        ActCustomTaskExt actCustomTaskExt = customTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, taskDefinitionKey);
        if (Objects.isNull(actCustomTaskExt)) {
            return Collections.emptyList();
        }
        Integer isAutoSelect = Optional.ofNullable(actCustomTaskExt.getIsAutoSelect()).orElse(1);
        if (isAutoSelect == 1) {
            return Collections.emptyList();
        }
        // 多实例是否最后一步，ture
        Boolean completeTask = multiInTaskService.isCompleteTask(task);
        if (!completeTask) {
            return Collections.emptyList();
        }
        Map<String, Object> busData = Optional.ofNullable(processParticipantsReqDTO.getBusData()).orElse(new HashMap<>(16));
        busData.put("__APPROVAL_TYPE", processParticipantsReqDTO.getApprovalType());
        List<FlowElement> targetFlowElements = getTargetFlowElements(execution, processDefinitionId, taskDefinitionKey, busData);
        Map<String, Object> variables = flowElementUtil.getVariables(busData, task.getProcessInstanceId());
        for (FlowElement flowElement : targetFlowElements) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                ProcessParticipantsInfoDTO processParticipantsInfoDTO =  buildProcessParticipantsInfo(userTask, processInstance, variables);
                result.add(processParticipantsInfoDTO);
            }
        }
        return result;
    }

    private ProcessParticipantsInfoDTO buildProcessParticipantsInfo(UserTask userTask, ProcessInstance processInstance, Map<String, Object> variables) {


        ProcessParticipantsInfoDTO processParticipantsInfoDTO = buildProcessParticipantsInfo(userTask, processInstance.getProcessDefinitionId());

        // Extract user values from options
        List<String> userList = processParticipantsInfoDTO.getOptions().stream()
                .filter(p -> StrUtil.equalsIgnoreCase(p.getTitle(), "用户"))
                .findFirst()
                .map(ProcessParticipantsInfoDTO::getData)
                .orElse(Collections.emptyList())
                .stream()
                .map(SysUserModel::getValue)
                .collect(Collectors.toList());

        ActCustomUser customUserByTaskInfo = actCustomUserService.getActCustomUserByTaskInfo(processInstance.getProcessDefinitionId(), userTask.getId(), FlowConstant.USER_TYPE_0);
        List<String> resultList = relationSelectUser.getUserList(customUserByTaskInfo,variables, processInstance);
        // Filter out existing users from the result list
        if (CollUtil.isNotEmpty(resultList)) {
            resultList.removeAll(userList);
            String[] array = resultList.stream().toArray(String[]::new);
            List<SysUserModel> data = Optional.ofNullable(sysBaseAPI.queryUserByNames(array)).orElse(Collections.emptyList()).stream()
                    .filter(Objects::nonNull).map(this::buildSysUserModel).collect(Collectors.toList());


            List<ProcessParticipantsInfoDTO> options = processParticipantsInfoDTO.getOptions();
            ProcessParticipantsInfoDTO userOption = options.stream()
                    .filter(p -> StrUtil.equalsIgnoreCase(p.getTitle(), "用户"))
                    .findFirst()
                    .orElse(null);

            if (CollUtil.isNotEmpty(data)) {
                if (userOption != null) {
                    userOption.getData().addAll(data);
                } else {
                    ProcessParticipantsInfoDTO newUserOption = new ProcessParticipantsInfoDTO();
                    newUserOption.setTitle("用户");
                    newUserOption.setData(data);
                    options.add(newUserOption);
                }
            }
        }
        return processParticipantsInfoDTO;
    }


    /**
     * 构建流程参与者信息 DTO
     *
     * @param userTask            用户任务
     * @param processDefinitionId 流程定义 ID
     * @return 构建好的流程参与者信息 DTO
     */
    private ProcessParticipantsInfoDTO buildProcessParticipantsInfo(UserTask userTask, String processDefinitionId) {
        ProcessParticipantsInfoDTO processParticipantsInfoDTO = new ProcessParticipantsInfoDTO();
        processParticipantsInfoDTO.setTitle(userTask.getName());
        processParticipantsInfoDTO.setNodeId(userTask.getId());
        processParticipantsInfoDTO.setOptions(new ArrayList<>());

        String nextTaskId = userTask.getId();
        ActCustomUser customUserByTaskInfo = actCustomUserService.getActCustomUserByTaskInfo(processDefinitionId, nextTaskId, FlowConstant.USER_TYPE_0);

        if (customUserByTaskInfo != null) {
            // 构建用户参与者信息
            buildUserParticipantsInfo(customUserByTaskInfo.getUserName(), processParticipantsInfoDTO.getOptions());

            // 构建部门参与者信息
            buildDepartParticipantsInfo(customUserByTaskInfo.getOrgId(), processParticipantsInfoDTO.getOptions());

            // 构建角色参与者信息
            buildRoleParticipantsInfo(customUserByTaskInfo.getRoleCode(), processParticipantsInfoDTO.getOptions());

            // 构建岗位参与者信息
            buildPostParticipantsInfo(customUserByTaskInfo.getPost(), processParticipantsInfoDTO.getOptions());
        }

        return processParticipantsInfoDTO;
    }

    /**
     * 获取目标流程元素列表，根据当前执行实例和任务定义键
     *
     * @param execution           执行实例
     * @param processDefinitionId 流程定义 ID
     * @param taskDefinitionKey   任务定义键
     * @return 目标流程元素列表
     */
    private List<FlowElement> getTargetFlowElements(Execution execution, String processDefinitionId, String taskDefinitionKey, Map<String,Object> busData) {
        // 获取源流程元素
        FlowElement sourceFlowElement = flowElementUtil.getFlowElement(processDefinitionId, taskDefinitionKey);

        // 获取目标流程元素列表
        return flowElementUtil.getTargetFlowElement(execution, sourceFlowElement, busData);
    }

    /**
     * 构建用户维度的流程参与者信息
     *
     * @param userNameStr 用户字符创
     * @param result     结果列表，用于存储构建的参与者信息对象
     */
    private void buildUserParticipantsInfo(String userNameStr, List<ProcessParticipantsInfoDTO> result) {
        if(StrUtil.isEmpty(userNameStr)){
            return;
        }

        String[] userNames = StrUtil.split(userNameStr, ",");
        List<LoginUser> loginUsers = sysBaseAPI.queryUserByNames(userNames);
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

    /**
     * 构建部门维度的流程参与者信息
     *
     * @param departIdStr 部门id字符串，多个使用","分割
     * @param result      结果列表，用于存储构建的参与者信息对象
     */
    private void buildDepartParticipantsInfo(String departIdStr, List<ProcessParticipantsInfoDTO> result) {
        if (StrUtil.isEmpty(departIdStr)) {
            return;
        }

        List<SysUserModel> sysUserModels = sysBaseAPI.queryDepartUserTree(departIdStr);
        if (CollUtil.isNotEmpty(sysUserModels)) {
            ProcessParticipantsInfoDTO processParticipantsInfoDTO = new ProcessParticipantsInfoDTO();
            processParticipantsInfoDTO.setTitle("部门");
            processParticipantsInfoDTO.setData(sysUserModels);
            result.add(processParticipantsInfoDTO);
        }
    }

    /**
     * 构建角色维度的流程参与者信息
     *
     * @param roleCodes 角色编码字符串，多个使用","分割
     * @param result    结果列表，用于存储构建的参与者信息对象
     */
    private void buildRoleParticipantsInfo(String roleCodes, List<ProcessParticipantsInfoDTO> result) {
        if (StrUtil.isEmpty(roleCodes)) {
            return;
        }

        List<SysUserModel> sysUserModels = sysBaseAPI.queryRoleUserTree(roleCodes,Boolean.TRUE, Boolean.TRUE);
        if (CollUtil.isNotEmpty(sysUserModels)) {
            ProcessParticipantsInfoDTO processParticipantsInfoDTO = new ProcessParticipantsInfoDTO();
            processParticipantsInfoDTO.setTitle("角色");
            processParticipantsInfoDTO.setData(sysUserModels);
            result.add(processParticipantsInfoDTO);
        }
    }

    /**
     * 构建岗位维度的流程参与者信息
     *
     * @param postCodes 岗位编码字符串，多个使用","分割
     * @param result    结果列表，用于存储构建的参与者信息对象
     */
    private void buildPostParticipantsInfo(String postCodes, List<ProcessParticipantsInfoDTO> result) {
        if (StrUtil.isEmpty(postCodes)) {
            return;
        }

        List<SysUserModel> sysUserModels = sysBaseAPI.queryPostUserTree(postCodes, Boolean.TRUE, Boolean.TRUE);
        if (CollUtil.isNotEmpty(sysUserModels)) {
            ProcessParticipantsInfoDTO processParticipantsInfoDTO = new ProcessParticipantsInfoDTO();
            processParticipantsInfoDTO.setTitle("岗位");
            processParticipantsInfoDTO.setData(sysUserModels);
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

    private List<FlowElement> getChildUserTaskList(
            FlowElement source, List<String> runActiveIdList, Set<String> hasSequenceFlow, List<FlowElement> flowElementList) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        flowElementList = flowElementList == null ? new ArrayList<>() : flowElementList;
        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof EndEvent && source.getSubProcess() != null) {
            flowElementList = getChildUserTaskList(
                    source.getSubProcess(), runActiveIdList, hasSequenceFlow, flowElementList);
        }
        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);
        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 如果为用户任务类型，或者为网关
                // 活动节点ID 在运行的任务中存在，添加
                FlowElement targetElement = sequenceFlow.getTargetFlowElement();
                if ((targetElement instanceof UserTask || targetElement instanceof Gateway)
                        && runActiveIdList.contains(targetElement.getId())) {
                    flowElementList.add(sequenceFlow.getTargetFlowElement());
                    continue;
                }
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    List<FlowElement> childUserTaskList = getChildUserTaskList(
                            (FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), runActiveIdList, hasSequenceFlow, null);
                    // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
                    if (childUserTaskList != null && childUserTaskList.size() > 0) {
                        flowElementList.addAll(childUserTaskList);
                        continue;
                    }
                }
                // 继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                flowElementList = getChildUserTaskList(
                        sequenceFlow.getTargetFlowElement(), runActiveIdList, new HashSet<>(hasSequenceFlow), flowElementList);
            }
        }
        return flowElementList;
    }

    private Collection<FlowElement> getAllElements(Collection<FlowElement> flowElements, Collection<FlowElement> allElements) {
        allElements = allElements == null ? new ArrayList<>() : allElements;
        for (FlowElement flowElement : flowElements) {
            allElements.add(flowElement);
            if (flowElement instanceof SubProcess) {
                allElements = getAllElements(((SubProcess) flowElement).getFlowElements(), allElements);
            }
        }
        return allElements;
    }

    private List<String> cleanHistoricTaskInstance(
            Collection<FlowElement> allElements, List<HistoricActivityInstance> historicActivityList) {
        // 会签节点收集
        List<String> multiTask = new ArrayList<>();
        allElements.forEach(flowElement -> {
            if (flowElement instanceof UserTask) {
                // 如果该节点的行为为会签行为，说明该节点为会签节点
                if (((UserTask) flowElement).getBehavior() instanceof ParallelMultiInstanceBehavior
                        || ((UserTask) flowElement).getBehavior() instanceof SequentialMultiInstanceBehavior) {
                    multiTask.add(flowElement.getId());
                }
            }
        });
        // 循环放入栈，栈 LIFO：后进先出
        Stack<HistoricActivityInstance> stack = new Stack<>();
        historicActivityList.forEach(stack::push);
        // 清洗后的历史任务实例
        List<String> lastHistoricTaskInstanceList = new ArrayList<>();
        // 网关存在可能只走了部分分支情况，且还存在跳转废弃数据以及其他分支数据的干扰，因此需要对历史节点数据进行清洗
        // 临时用户任务 key
        StringBuilder userTaskKey = null;
        // 临时被删掉的任务 key，存在并行情况
        List<String> deleteKeyList = new ArrayList<>();
        // 临时脏数据线路
        List<Set<String>> dirtyDataLineList = new ArrayList<>();
        // 由某个点跳到会签点,此时出现多个会签实例对应 1 个跳转情况，需要把这些连续脏数据都找到
        // 会签特殊处理下标
        int multiIndex = -1;
        // 会签特殊处理 key
        StringBuilder multiKey = null;
        // 会签特殊处理操作标识
        boolean multiOpera = false;
        while (!stack.empty()) {
            // 从这里开始 userTaskKey 都还是上个栈的 key
            // 是否是脏数据线路上的点
            final boolean[] isDirtyData = {false};
            for (Set<String> oldDirtyDataLine : dirtyDataLineList) {
                if (oldDirtyDataLine.contains(stack.peek().getActivityId())) {
                    isDirtyData[0] = true;
                }
            }
            // 删除原因不为空，说明从这条数据开始回跳或者回退的
            // MI_END：会签完成后，其他未签到节点的删除原因，不在处理范围内
            if (stack.peek().getDeleteReason() != null && !"MI_END".equals(stack.peek().getDeleteReason())) {
                // 可以理解为脏线路起点
                String dirtyPoint = "";
                if (stack.peek().getDeleteReason().contains("Change activity to ")) {
                    dirtyPoint = stack.peek().getDeleteReason().replace("Change activity to ", "");
                }
                // 会签回退删除原因有点不同
                if (stack.peek().getDeleteReason().contains("Change parent activity to ")) {
                    dirtyPoint = stack.peek().getDeleteReason().replace("Change parent activity to ", "");
                }
                FlowElement dirtyTask = null;
                // 获取变更节点的对应的入口处连线
                // 如果是网关并行回退情况，会变成两条脏数据路线，效果一样
                for (FlowElement flowElement : allElements) {
                    if (flowElement.getId().equals(stack.peek().getActivityId())) {
                        dirtyTask = flowElement;
                    }
                }
                // 获取脏数据线路
                Set<String> dirtyDataLine = findDirtyRoads(
                        dirtyTask, null, null, StrUtil.split(dirtyPoint, ','), null);
                // 自己本身也是脏线路上的点，加进去
                dirtyDataLine.add(stack.peek().getActivityId());
                log.info(stack.peek().getActivityId() + "点脏路线集合：" + dirtyDataLine);
                // 是全新的需要添加的脏线路
                boolean isNewDirtyData = true;
                for (Set<String> strings : dirtyDataLineList) {
                    // 如果发现他的上个节点在脏线路内，说明这个点可能是并行的节点，或者连续驳回
                    // 这时，都以之前的脏线路节点为标准，只需合并脏线路即可，也就是路线补全
                    if (strings.contains(userTaskKey.toString())) {
                        isNewDirtyData = false;
                        strings.addAll(dirtyDataLine);
                    }
                }
                // 已确定时全新的脏线路
                if (isNewDirtyData) {
                    // deleteKey 单一路线驳回到并行，这种同时生成多个新实例记录情况，这时 deleteKey 其实是由多个值组成
                    // 按照逻辑，回退后立刻生成的实例记录就是回退的记录
                    // 至于驳回所生成的 Key，直接从删除原因中获取，因为存在驳回到并行的情况
                    deleteKeyList.add(dirtyPoint + ",");
                    dirtyDataLineList.add(dirtyDataLine);
                }
                // 添加后，现在这个点变成脏线路上的点了
                isDirtyData[0] = true;
            }
            // 如果不是脏线路上的点，说明是有效数据，添加历史实例 Key
            if (!isDirtyData[0]) {
                lastHistoricTaskInstanceList.add(stack.peek().getActivityId());
            }
            // 校验脏线路是否结束
            for (int i = 0; i < deleteKeyList.size(); i++) {
                // 如果发现脏数据属于会签，记录下下标与对应 Key，以备后续比对，会签脏数据范畴开始
                if (multiKey == null && multiTask.contains(stack.peek().getActivityId())
                        && deleteKeyList.get(i).contains(stack.peek().getActivityId())) {
                    multiIndex = i;
                    multiKey = new StringBuilder(stack.peek().getActivityId());
                }
                // 会签脏数据处理，节点退回会签清空
                // 如果在会签脏数据范畴中发现 Key改变，说明会签脏数据在上个节点就结束了，可以把会签脏数据删掉
                if (multiKey != null && !multiKey.toString().equals(stack.peek().getActivityId())) {
                    deleteKeyList.set(multiIndex, deleteKeyList.get(multiIndex).replace(stack.peek().getActivityId() + ",", ""));
                    multiKey = null;
                    // 结束进行下校验删除
                    multiOpera = true;
                }
                // 其他脏数据处理
                // 发现该路线最后一条脏数据，说明这条脏数据线路处理完了，删除脏数据信息
                // 脏数据产生的新实例中是否包含这条数据
                if (multiKey == null && deleteKeyList.get(i).contains(stack.peek().getActivityId())) {
                    // 删除匹配到的部分
                    deleteKeyList.set(i, deleteKeyList.get(i).replace(stack.peek().getActivityId() + ",", ""));
                }
                // 如果每组中的元素都以匹配过，说明脏数据结束
                if ("".equals(deleteKeyList.get(i))) {
                    // 同时删除脏数据
                    deleteKeyList.remove(i);
                    dirtyDataLineList.remove(i);
                    break;
                }
            }
            // 会签数据处理需要在循环外处理，否则可能导致溢出
            // 会签的数据肯定是之前放进去的所以理论上不会溢出，但还是校验下
            if (multiOpera && deleteKeyList.size() > multiIndex && "".equals(deleteKeyList.get(multiIndex))) {
                // 同时删除脏数据
                deleteKeyList.remove(multiIndex);
                dirtyDataLineList.remove(multiIndex);
                multiIndex = -1;
                multiOpera = false;
            }
            // pop() 方法与 peek() 方法不同，在返回值的同时，会把值从栈中移除
            // 保存新的 userTaskKey 在下个循环中使用
            userTaskKey = new StringBuilder(stack.pop().getActivityId());
        }
        log.info("清洗后的历史节点数据：" + lastHistoricTaskInstanceList);
        return lastHistoricTaskInstanceList;
    }

    private Set<String> findDirtyRoads(
            FlowElement source, List<String> passRoads, Set<String> hasSequenceFlow, List<String> targets, Set<String> dirtyRoads) {
        passRoads = passRoads == null ? new ArrayList<>() : passRoads;
        dirtyRoads = dirtyRoads == null ? new HashSet<>() : dirtyRoads;
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof StartEvent && source.getSubProcess() != null) {
            dirtyRoads = findDirtyRoads(source.getSubProcess(), passRoads, hasSequenceFlow, targets, dirtyRoads);
        }
        // 根据类型，获取入口连线
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(source);
        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 新增经过的路线
                passRoads.add(sequenceFlow.getSourceFlowElement().getId());
                // 如果此点为目标点，确定经过的路线为脏线路，添加点到脏线路中，然后找下个连线
                if (targets.contains(sequenceFlow.getSourceFlowElement().getId())) {
                    dirtyRoads.addAll(passRoads);
                    continue;
                }
                // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
                if (sequenceFlow.getSourceFlowElement() instanceof SubProcess) {
                    dirtyRoads = findChildProcessAllDirtyRoad(
                            (StartEvent) ((SubProcess) sequenceFlow.getSourceFlowElement()).getFlowElements().toArray()[0], null, dirtyRoads);
                    // 是否存在子流程上，true 是，false 否
                    Boolean isInChildProcess = dirtyTargetInChildProcess(
                            (StartEvent) ((SubProcess) sequenceFlow.getSourceFlowElement()).getFlowElements().toArray()[0], null, targets, null);
                    if (isInChildProcess) {
                        // 已在子流程上找到，该路线结束
                        continue;
                    }
                }
                // 继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                dirtyRoads = findDirtyRoads(sequenceFlow.getSourceFlowElement(),
                        new ArrayList<>(passRoads), new HashSet<>(hasSequenceFlow), targets, dirtyRoads);
            }
        }
        return dirtyRoads;
    }

    private Boolean dirtyTargetInChildProcess(
            FlowElement source, Set<String> hasSequenceFlow, List<String> targets, Boolean inChildProcess) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        inChildProcess = inChildProcess == null ? false : inChildProcess;
        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);
        if (sequenceFlows != null && !inChildProcess) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 如果发现目标点在子流程上存在，说明只到子流程为止
                if (targets.contains(sequenceFlow.getTargetFlowElement().getId())) {
                    inChildProcess = true;
                    break;
                }
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    inChildProcess = dirtyTargetInChildProcess((FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), hasSequenceFlow, targets, inChildProcess);
                }
                // 继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                inChildProcess = dirtyTargetInChildProcess(sequenceFlow.getTargetFlowElement(), new HashSet<>(hasSequenceFlow), targets, inChildProcess);
            }
        }
        return inChildProcess;
    }

    private Set<String> findChildProcessAllDirtyRoad(
            FlowElement source, Set<String> hasSequenceFlow, Set<String> dirtyRoads) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        dirtyRoads = dirtyRoads == null ? new HashSet<>() : dirtyRoads;
        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);
        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 添加脏路线
                dirtyRoads.add(sequenceFlow.getTargetFlowElement().getId());
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    dirtyRoads = findChildProcessAllDirtyRoad(
                            (FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), hasSequenceFlow, dirtyRoads);
                }
                // 继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                dirtyRoads = findChildProcessAllDirtyRoad(
                        sequenceFlow.getTargetFlowElement(), new HashSet<>(hasSequenceFlow), dirtyRoads);
            }
        }
        return dirtyRoads;
    }

    private List<UserTask> getParentUserTaskList(
            FlowElement source, Set<String> hasSequenceFlow, List<UserTask> userTaskList) {
        userTaskList = userTaskList == null ? new ArrayList<>() : userTaskList;
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof StartEvent && source.getSubProcess() != null) {
            userTaskList = getParentUserTaskList(source.getSubProcess(), hasSequenceFlow, userTaskList);
        }
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(source);
        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 类型为用户节点，则新增父级节点
                if (sequenceFlow.getSourceFlowElement() instanceof UserTask) {
                    userTaskList.add((UserTask) sequenceFlow.getSourceFlowElement());
                    continue;
                }
                // 类型为子流程，则添加子流程开始节点出口处相连的节点
                if (sequenceFlow.getSourceFlowElement() instanceof SubProcess) {
                    // 获取子流程用户任务节点
                    List<UserTask> childUserTaskList = findChildProcessUserTasks(
                            (StartEvent) ((SubProcess) sequenceFlow.getSourceFlowElement()).getFlowElements().toArray()[0], null, null);
                    // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
                    if (childUserTaskList != null && childUserTaskList.size() > 0) {
                        userTaskList.addAll(childUserTaskList);
                        continue;
                    }
                }
                // 网关场景的继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                userTaskList = getParentUserTaskList(
                        sequenceFlow.getSourceFlowElement(), new HashSet<>(hasSequenceFlow), userTaskList);
            }
        }
        return userTaskList;
    }

    private List<UserTask> findChildProcessUserTasks(FlowElement source, Set<String> hasSequenceFlow, List<UserTask> userTaskList) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        userTaskList = userTaskList == null ? new ArrayList<>() : userTaskList;
        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);
        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 如果为用户任务类型，且任务节点的 Key 正在运行的任务中存在，添加
                if (sequenceFlow.getTargetFlowElement() instanceof UserTask) {
                    userTaskList.add((UserTask) sequenceFlow.getTargetFlowElement());
                    continue;
                }
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    List<UserTask> childUserTaskList = findChildProcessUserTasks((FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), hasSequenceFlow, null);
                    // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
                    if (childUserTaskList != null && childUserTaskList.size() > 0) {
                        userTaskList.addAll(childUserTaskList);
                        continue;
                    }
                }
                // 继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                userTaskList = findChildProcessUserTasks(sequenceFlow.getTargetFlowElement(), new HashSet<>(hasSequenceFlow), userTaskList);
            }
        }
        return userTaskList;
    }

    private List<SequenceFlow> getElementOutgoingFlows(FlowElement source) {
        List<SequenceFlow> sequenceFlows = null;
        if (source instanceof org.flowable.bpmn.model.Task) {
            sequenceFlows = ((org.flowable.bpmn.model.Task) source).getOutgoingFlows();
        } else if (source instanceof Gateway) {
            sequenceFlows = ((Gateway) source).getOutgoingFlows();
        } else if (source instanceof SubProcess) {
            sequenceFlows = ((SubProcess) source).getOutgoingFlows();
        } else if (source instanceof StartEvent) {
            sequenceFlows = ((StartEvent) source).getOutgoingFlows();
        } else if (source instanceof EndEvent) {
            sequenceFlows = ((EndEvent) source).getOutgoingFlows();
        }
        return sequenceFlows;
    }

    private List<SequenceFlow> getElementIncomingFlows(FlowElement source) {
        List<SequenceFlow> sequenceFlows = null;
        if (source instanceof org.flowable.bpmn.model.Task) {
            sequenceFlows = ((org.flowable.bpmn.model.Task) source).getIncomingFlows();
        } else if (source instanceof Gateway) {
            sequenceFlows = ((Gateway) source).getIncomingFlows();
        } else if (source instanceof SubProcess) {
            sequenceFlows = ((SubProcess) source).getIncomingFlows();
        } else if (source instanceof StartEvent) {
            sequenceFlows = ((StartEvent) source).getIncomingFlows();
        } else if (source instanceof EndEvent) {
            sequenceFlows = ((EndEvent) source).getIncomingFlows();
        }
        return sequenceFlows;
    }




    /**
     * @param processParticipantsReqDTO
     * @return
     */
    @Override
    public List<ProcessParticipantsInfoDTO> getProcessParticipantsInfoWithOutStart(ProcessParticipantsReqDTO processParticipantsReqDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<ProcessParticipantsInfoDTO> result = new ArrayList<>();
        String modelKey = processParticipantsReqDTO.getModelKey();
        // 获取流程定义信息
        ProcessDefinition processDefinition = flowElementUtil.getProcessDefinition(modelKey);
        // 获取第一个用户节点信息
        UserTask userTask = flowElementUtil.getFirstUserTaskByModelKey(modelKey);
        String processDefinitionId = processDefinition.getId();
        String nodeId = userTask.getId();

        // 是否自动选人
        ActCustomTaskExt actCustomTaskExt = customTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, nodeId);
        if (Objects.isNull(actCustomTaskExt)) {
            return Collections.emptyList();
        }

        Integer isAutoSelect = Optional.ofNullable(actCustomTaskExt.getIsAutoSelect()).orElse(1);
        if (isAutoSelect == 1) {
            return Collections.emptyList();
        }
        Map<String, Object> busData = Optional.ofNullable(processParticipantsReqDTO.getBusData()).orElse(new HashMap<>(16));
        busData.put("__APPROVAL_TYPE", processParticipantsReqDTO.getApprovalType());
        List<FlowElement> flowElementList = flowElementUtil.getTargetFlowElement(modelKey, userTask, busData);
        ExecutionEntityImpl processInstance = new ExecutionEntityImpl();
        processInstance.setStartUserId(loginUser.getUsername());
        processInstance.setProcessDefinitionId(processDefinitionId);
        Map<String, Object> variables = flowElementUtil.getVariablesByModelKey(busData, modelKey);
        for (FlowElement flowElement : flowElementList) {
            if (flowElement instanceof UserTask) {
                UserTask task = (UserTask) flowElement;
                ProcessParticipantsInfoDTO processParticipantsInfoDTO = buildProcessParticipantsInfo(task, processInstance, variables);
                result.add(processParticipantsInfoDTO);
            }
        }
        return result;
    }


    /**
     * 根据流程实例获取流程记录
     *
     * @param processInstanceId
     * @return
     */
    @Override
    public List<ProcessRecordDTO> getHistoricLogByProcessInstanceIdV1(String processInstanceId) {

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        FlowElement endEvent = flowElementUtil.getEndFlowElementByDefinitionId(processDefinitionId);
        UserTask userTask = flowElementUtil.getFirstUserTaskByDefinitionId(processDefinitionId);

        LinkedHashMap<String, HistoryTaskInfo> recordMap = flowForecastService.mergeTask(processInstanceId);

        List<ActCustomTaskComment> flowTaskCommentList = taskCommentService.getFlowTaskCommentList(processInstanceId);
        Map<String, String> commentMap = flowTaskCommentList.stream()
                .filter(actCustomTaskComment -> StrUtil.isNotBlank(actCustomTaskComment.getTaskId()))
                .collect(Collectors.toMap(ActCustomTaskComment::getTaskId,
                        actCustomTaskComment -> Optional.ofNullable(actCustomTaskComment.getComment()).orElse(""), (t1,t2)->t1));

        Map<String, ActCustomTaskComment> taskCommentMap = flowTaskCommentList.stream().filter(actCustomTaskComment -> StrUtil.isNotBlank(actCustomTaskComment.getTaskId()))
                .collect(Collectors.toMap(ActCustomTaskComment::getTaskId, t -> t, (t1, t2) -> t1));


        Set<String> userNameSet = recordMap.values().stream().map(HistoryTaskInfo::getList).flatMap(List::stream)
                .filter(historicTaskInstance -> StrUtil.isNotBlank(historicTaskInstance.getAssignee()))
                .map(HistoricTaskInstance::getAssignee).collect(Collectors.toSet());
        List<DictModel> dictModelList = sysBaseAPI.getDictItems("sys_post");
        Map<String, String> sysPostMap = dictModelList.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (t1, t2) -> t1));
        List<LoginUser> loginUserList = sysBaseAPI.getLoginUserList(new ArrayList<>(userNameSet));
        Map<String, LoginUser> userMap = loginUserList.stream().collect(Collectors.toMap(LoginUser::getUsername, t->t, (t1, t2) -> t1));
        LoginUser user = new LoginUser();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(EmptyRuleEnum.AUTO_COMPLETE.getMessage());
        user.setRealname("自动通过");
        userMap.put(EmptyRuleEnum.AUTO_COMPLETE.getMessage(), user);
        List<ProcessRecordDTO> dtoList = recordMap.keySet().stream().map(key -> {
            HistoryTaskInfo historyTaskInfo = recordMap.get(key);
            ProcessRecordDTO recordDTO = ProcessRecordDTO.builder()
                    .nodeId(historyTaskInfo.getTaskDefinitionKey())
                    .nodeName(historyTaskInfo.getName())
                    .build();

            List<HistoricTaskInstance> taskInfoList = historyTaskInfo.getList();
            taskInfoList = taskInfoList.stream().filter(historicTaskInstance -> (Objects.nonNull(historicTaskInstance.getClaimTime())
                    || Objects.isNull(historicTaskInstance.getEndTime()))).collect(Collectors.toList());

            List<HistoricTaskInstance> unFinishList = taskInfoList.stream().filter(historicTaskInstance -> Objects.isNull(historicTaskInstance.getEndTime()))
                    .collect(Collectors.toList());
            // 第一个任务
            if (StrUtil.equalsIgnoreCase(userTask.getId(), historyTaskInfo.getTaskDefinitionKey())) {
                //
                if (CollUtil.isNotEmpty(unFinishList)) {
                    recordDTO.setStateName("待提交");
                    recordDTO.setStateColor("#FFA800");
                } else {
                    recordDTO.setStateName("已提交");
                    recordDTO.setStateColor("#10C443");
                }
            } else {
                // 其他节点
                if (CollUtil.isNotEmpty(unFinishList)) {
                    if (unFinishList.size() == taskInfoList.size()) {
                        recordDTO.setStateName("待审批");
                        recordDTO.setStateColor("#FFA800");
                    } else {
                        recordDTO.setStateName("审批中");
                        recordDTO.setStateColor("#1890FF");
                    }
                } else {
                    recordDTO.setStateName("已通过");
                    recordDTO.setStateColor("#10C443");

                    // 终止流程，撤回
                    List<String> deleteReasonList = taskInfoList.stream().map(HistoricTaskInstance::getDeleteReason).collect(Collectors.toList());

                    boolean stopFlag = false;
                    // 终止流程
                    if (Objects.nonNull(endEvent)) {
                        stopFlag = deleteReasonList.stream().anyMatch(deleteReason -> StrUtil.contains(deleteReason, endEvent.getId()));
                    }

                    // 回退流程
                    boolean changeFlag = deleteReasonList.stream().anyMatch(deleteReason -> StrUtil.startWith(deleteReason,"Change"));
                    if (stopFlag) {
                        recordDTO.setStateName("已作废");
                        recordDTO.setStateColor("#FF0000");
                    } else {
                        if (changeFlag) {
                            recordDTO.setStateName("已退回");
                            recordDTO.setStateColor("#FFA800");
                        }
                    }

                    // 是否全部自动提交
                    List<HistoricTaskInstance> autoCompleteList = taskInfoList.stream().filter(historicTaskInstance -> {
                        ActCustomTaskComment actCustomTaskComment = taskCommentMap.get(historicTaskInstance.getId());
                        if (Objects.isNull(actCustomTaskComment)) {
                            return false;
                        }
                        return StrUtil.equalsIgnoreCase(FlowApprovalType.AUTO_COMPLETE, actCustomTaskComment.getApprovalType());
                    }).collect(Collectors.toList());

                    if (CollUtil.isNotEmpty(autoCompleteList)) {
                        if (autoCompleteList.size() == taskInfoList.size()) {
                            recordDTO.setStateName("自动通过");
                            recordDTO.setStateColor("#10C443");
                        }
                    }
                }
            }

            List<ProcessRecordNodeInfoDTO> infoDTOList = taskInfoList.stream().map(historicTaskInstance -> {
                ProcessRecordNodeInfoDTO nodeInfoDTO = ProcessRecordNodeInfoDTO.builder()
                        .endTime(historicTaskInstance.getEndTime())
                        .reason(commentMap.get(historicTaskInstance.getId()))
                        .build();
                String assignee = historicTaskInstance.getAssignee();
                if (StrUtil.isNotBlank(assignee)) {
                    LoginUser loginUser = userMap.get(assignee);
                    if (Objects.nonNull(loginUser)) {
                        String orgName = loginUser.getOrgName();
                        String jobName = Optional.ofNullable(loginUser.getJobName()).orElse("");
                        Set<String> jonSet = StrUtil.split(jobName, ',').stream().map(sysPostMap::get).collect(Collectors.toSet());
                        nodeInfoDTO.setRealName(loginUser.getRealname());
                        nodeInfoDTO.setUserName(loginUser.getUsername());

                        nodeInfoDTO.setUserInfo(String.format("%s；%s",   orgName, StrUtil.join(",", jonSet)));
                    }
                } else {
                    // 兼容历史数据

                }
                return nodeInfoDTO;
            }).collect(Collectors.toList());
            recordDTO.setNodeList(infoDTOList);
            return recordDTO;
        }).collect(Collectors.toList());
        Collections.reverse(dtoList);
        return dtoList;
    }
}
