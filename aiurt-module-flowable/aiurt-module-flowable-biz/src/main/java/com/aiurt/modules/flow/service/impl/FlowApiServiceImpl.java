package com.aiurt.modules.flow.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.entity.CustomTaskComment;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.utils.ReflectionService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        // 1. 验证流程定义数据的合法性。
        Result<ProcessDefinition> processDefinitionResult = flowElementUtil.verifyAndGetFlowEntry(startBpmnDTO.getModelKey());
        if (!processDefinitionResult.isSuccess()) {
            throw new AiurtBootException(processDefinitionResult.getMessage());
        }

        ProcessDefinition result = processDefinitionResult.getResult();
        if (!result.isSuspended()) {
            throw new AiurtBootException("当前流程定义已被挂起，不能启动新流程！");
        }

        // 2.判断是否是动态表单


        // 3.保存中间业务数据
        try {
            reflectionService.invokeService(null,null,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4.设置流程变量
        Map<String, Object> busData = startBpmnDTO.getBusData();
        busData.put(FlowConstant.PROC_INSTANCE_INITIATOR_VAR, loginUser.getUsername());
        busData.put(FlowConstant.PROC_INSTANCE_START_USER_NAME_VAR, loginUser.getUsername());

        // 5.启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(startBpmnDTO.getModelKey(),busData);

        // 6.获取流程启动后的第一个任务
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();
        if (StrUtil.equalsAny(task.getAssignee(), loginUser.getUsername(), FlowConstant.START_USER_NAME_VAR)) {
            // 按照规则，调用该方法的用户，就是第一个任务的assignee，因此默认会自动执行complete。
            if(ObjectUtil.isNotEmpty(startBpmnDTO.getCustomTaskComment())){
                startBpmnDTO.getCustomTaskComment().fillWith(task);
            }
            this.completeTask(task, startBpmnDTO.getCustomTaskComment(), startBpmnDTO.getBusData());
        }
        return processInstance;
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
     * @param task     工作流任务对象。
     * @param comment  审批对象。
     * @param busData 流程任务的变量数据。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void completeTask(Task task, CustomTaskComment comment, Map<String, Object> busData) {
        JSONObject passCopyData = null;
//        if (taskVariableData != null) {
//            passCopyData = (JSONObject) taskVariableData.remove(FlowConstant.COPY_DATA_KEY);
//        }
        if (comment != null) {
            // 处理多实例会签逻辑。
//            if (comment.getApprovalType().equals(FlowApprovalType.MULTI_SIGN)) {
//                String loginName = TokenData.takeFromRequest().getLoginName();
//                if (taskVariableData == null) {
//                    taskVariableData = new JSONObject();
//                }
//                String assigneeList = taskVariableData.getString(FlowConstant.MULTI_ASSIGNEE_LIST_VAR);
//                if (StrUtil.isBlank(assigneeList)) {
//                    FlowTaskExt flowTaskExt = flowTaskExtService.getByProcessDefinitionIdAndTaskId(
//                            task.getProcessDefinitionId(), task.getTaskDefinitionKey());
//                    assigneeList = this.buildMutiSignAssigneeList(flowTaskExt.getOperationListJson());
//                    if (assigneeList != null) {
//                        taskVariableData.put(FlowConstant.MULTI_ASSIGNEE_LIST_VAR, StrUtil.split(assigneeList,','));
//                    }
//                }
//                Assert.isTrue(StrUtil.isNotBlank(assigneeList));
//                taskVariableData.put(FlowConstant.MULTI_AGREE_COUNT_VAR, 0);
//                taskVariableData.put(FlowConstant.MULTI_REFUSE_COUNT_VAR, 0);
//                taskVariableData.put(FlowConstant.MULTI_ABSTAIN_COUNT_VAR, 0);
//                taskVariableData.put(FlowConstant.MULTI_SIGN_NUM_OF_INSTANCES_VAR, 0);
//                taskVariableData.put(FlowConstant.MULTI_SIGN_START_TASK_VAR, task.getId());
//                String comment = String.format("用户 [%s] 会签 [%s]。", loginName, assigneeList);
//                comment.setComment(comment);
//            }
            // 处理转办。
//            if (FlowApprovalType.TRANSFER.equals(flowTaskComment.getApprovalType())) {
//                taskService.setAssignee(task.getId(), flowTaskComment.getDelegateAssginee());
//                flowTaskComment.fillWith(task);
//                flowTaskCommentService.saveNew(flowTaskComment);
//                return;
//            }
//            if (taskVariableData == null) {
//                taskVariableData = new JSONObject();
//            }
//            this.handleMultiInstanceApprovalType(
//                    task.getExecutionId(), flowTaskComment.getApprovalType(), taskVariableData);
//            taskVariableData.put(FlowConstant.OPERATION_TYPE_VAR, flowTaskComment.getApprovalType());
//            comment.fillWith(task);
//            flowTaskCommentService.saveNew(flowTaskComment);
        }
        // 判断当前完成执行的任务，是否存在抄送设置。
//        Object copyData = runtimeService.getVariable(
//                task.getProcessInstanceId(), FlowConstant.COPY_DATA_MAP_PREFIX + task.getTaskDefinitionKey());
//        if (copyData != null || passCopyData != null) {
//            JSONObject copyDataJson = this.mergeCopyData(copyData, passCopyData);
//            flowMessageService.saveNewCopyMessage(task, copyDataJson);
//        }
        taskService.complete(task.getId(), busData);
//        flowMessageService.updateFinishedStatusByTaskId(task.getId());
    }
}
