package com.aiurt.modules.complete.service.impl;
import java.util.Date;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.common.enums.MultiApprovalRuleEnum;
import com.aiurt.modules.complete.dto.CompleteTaskContext;
import com.aiurt.modules.complete.dto.FlowCompleteReqDTO;
import com.aiurt.modules.copy.entity.ActCustomProcessCopy;
import com.aiurt.modules.copy.service.IActCustomProcessCopyService;
import com.aiurt.modules.flow.dto.NextNodeUserDTO;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.multideal.dto.MultiDealDTO;
import com.aiurt.modules.multideal.service.IMultiInTaskService;
import com.aiurt.modules.multideal.service.IMultiInstanceDealService;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.impl.DefaultSelectUser;
import com.aiurt.modules.user.service.IActCustomUserService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Slf4j
@Service
public class CommonFlowTaskCompleteServiceImpl extends AbsFlowCompleteServiceImpl {

    private final String APPROVAL_TYPE = "__APPROVAL_TYPE";

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IActCustomTaskExtService taskExtService;

    @Autowired
    private IMultiInTaskService multiInTaskService;

    @Autowired
    private FlowElementUtil flowElementUtil;

    @Autowired
    private IActCustomUserService customUserService;

    @Autowired
    private IMultiInstanceDealService multiInstanceDealService;

    @Autowired
    private IActCustomProcessCopyService processCopyService;

    @Autowired
    private DefaultSelectUser defaultSelectUser;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * 始前处理
     *
     * @param taskContext
     * @Description: preDeal
     * @author fgw
     */
    @Override
    public void preDeal(CompleteTaskContext taskContext) {
        super.preDeal(taskContext);
    }

    /**
     * 构建上下文环境。获取当前任务、节点、流程等信息。
     *
     * @param taskContext
     * @Description: buildTaskContext
     * @author fgw
     */
    @Override
    public void buildTaskContext(CompleteTaskContext taskContext) {
        FlowCompleteReqDTO flowCompleteReqDTO = taskContext.getFlowCompleteReqDTO();
        String taskId = flowCompleteReqDTO.getTaskId();
        String processInstanceId = flowCompleteReqDTO.getProcessInstanceId();

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        Task task = taskService.createTaskQuery().taskId(taskId).processInstanceId(processInstanceId).singleResult();

        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();

        ActCustomTaskExt customTaskExt = taskExtService.getByProcessDefinitionIdAndTaskId(task.getProcessDefinitionId(), task.getTaskDefinitionKey());

        taskContext.setCurrentTask(task);
        taskContext.setProcessInstance(processInstance);
        taskContext.setExecutionEntity((ExecutionEntity) execution);
        taskContext.setMultiApprovalRule(customTaskExt.getUserType());

        // 构建流程变量
        Map<String, Object> variableData = flowCompleteReqDTO.getVariableData();
        if (Objects.isNull(variableData)) {
            variableData = new HashMap<>(16);
        }
        Map<String, Object> busData = Optional.ofNullable(flowCompleteReqDTO.getBusData()).orElse(new HashMap<>());
       // busData.put(APPROVAL_TYPE, approvalType);

        Map<String, Object> variables = flowElementUtil.getVariables(busData, processInstanceId);
        variableData.putAll(variables);
        taskContext.setVariableData(variableData);

        // 判断是否多实例的最后一步， 如果不是最后一个不需要提交, 但是不是多实例的时候需要提交，需要设置流程办理人
        Boolean completeTask = multiInTaskService.isCompleteTask(task);

        // 自动选人 1, 0 否
        Integer isAutoSelect = customTaskExt.getIsAutoSelect();
        // 办理规则 如果办理规则为空则，就是旧版流程选人，不需要处理, 现在新版也是存在
        // 多实例最后一步，自动选人则构造下一步节点以及下一个节点的数据,
        if (completeTask && Objects.nonNull(isAutoSelect) && isAutoSelect == 1) {
            // 如果单实例，或者识多实例最后一部办理人时需要自动选人
            log.info("当前活动是多少实例，且是多实例的最后一个活动，或者时单例任务，设置下一步办理人");
            FlowElement flowElement = flowElementUtil.getFlowElement(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            List<FlowElement> targetFlowElement = flowElementUtil.getTargetFlowElement(execution, flowElement, busData);
            Map<String, Object> finalVariableData = variableData;
            List<NextNodeUserDTO> nodeUserDTOList = targetFlowElement.stream().map(element -> {
                NextNodeUserDTO nextNodeUserDTO = new NextNodeUserDTO();
                nextNodeUserDTO.setNodeId(element.getId());
                ActCustomUser actCustomUser = customUserService.getActCustomUserByTaskInfo(task.getProcessDefinitionId(), element.getId(), "0");
                List<String> userList = defaultSelectUser.selectAllList(actCustomUser, processInstance, finalVariableData);
                nextNodeUserDTO.setApprover(userList);
                return nextNodeUserDTO;
            }).collect(Collectors.toList());
            flowCompleteReqDTO.setNextNodeUserParam(nodeUserDTOList);
        }

    }

    /**
     * 处理会签任务
     *
     * @param taskContext
     * @Description: dealSignTask
     * @author fgw
     */
    @Override
    public void dealSignTask(CompleteTaskContext taskContext) {
        super.dealSignTask(taskContext);
    }

    /**
     * 在任务执行完前处理下一个节点。设置下一个节点参数。
     *
     * @param taskContext
     * @Description: dealNextNodeBeforeComplete
     * @author fgw
     */
    @Override
    public void dealNextNodeBeforeComplete(CompleteTaskContext taskContext) {
        //多实例任务处理
        Task currentTask = taskContext.getCurrentTask();
        FlowCompleteReqDTO flowCompleteReqDTO = taskContext.getFlowCompleteReqDTO();
        MultiDealDTO multiDealDTO = new MultiDealDTO(flowCompleteReqDTO);
        log.info("多实例任务处理,活动（{}）",currentTask.getId());
        multiInstanceDealService.multiInstanceDeal(multiDealDTO);

    }

    /**
     * 执行complete操作
     *
     * @param taskContext
     * @Description: dealComplete
     * @author fgw
     */
    @Override
    public void dealComplete(CompleteTaskContext taskContext) {
        Task currentTask = taskContext.getCurrentTask();
        String processInstanceId = currentTask.getProcessInstanceId();
        String nodeId = currentTask.getTaskDefinitionKey();
        Map<String, Object> variableData = taskContext.getVariableData();
        taskService.complete(currentTask.getId(), variableData);

        // 如果任意会签， 则需要自动提交其他任务
        String multiApprovalRule = taskContext.getMultiApprovalRule();
        if (StrUtil.equalsIgnoreCase(multiApprovalRule, MultiApprovalRuleEnum.TASK_MULTI_INSTANCE_TYPE_1.getCode())) {
            //
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(nodeId).list();

            taskList.stream().forEach(task -> {
                task.setDescription("ANY_NODE");
                taskService.complete(task.getId());
            });
        }

    }

    /**
     * 完成后处理事件
     *
     * @param taskContext
     * @Description: afterDeal
     * @author fgw
     */
    @Override
    public void afterDeal(CompleteTaskContext taskContext) {
        ProcessInstance processInstance = taskContext.getProcessInstance();
        Task currentTask = taskContext.getCurrentTask();
        String nodeId = currentTask.getTaskDefinitionKey();
        String definitionId = currentTask.getProcessDefinitionId();
        List<String> userList = customUserService.getUserByTaskInfo(definitionId, nodeId, "1");

        String processInstanceId = currentTask.getProcessInstanceId();
        String assignee = currentTask.getAssignee();
        String taskId = currentTask.getId();

        List<ActCustomProcessCopy> copyList = userList.stream().map(userName -> {
            ActCustomProcessCopy copy = new ActCustomProcessCopy();
            copy.setProcessInstanceId(processInstanceId);
            copy.setTaskId(taskId);
            copy.setDelFlag(0);
            copy.setUserName(userName);
            return copy;
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(copyList)) {
            processCopyService.saveBatch(copyList);
        }
        if (CollUtil.isNotEmpty(userList)) {
            String startUserId = processInstance.getStartUserId();
            LoginUser loginUser = sysBaseAPI.getUserByName(startUserId);
            String format = DateUtil.format(processInstance.getStartTime(), "yyyy-MM-dd");
            // 发送消息
            HashMap<String, Object> map = new HashMap<>(16);
            map.put("creatBy", loginUser.getRealname());
            map.put("creatTime",format);

            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, processInstance.getBusinessKey());

            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.BPM.getType());

            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setIsMarkdown(true);
            messageDTO.setFromUser(assignee);
            messageDTO.setToUser(StrUtil.join(",", userList));
            messageDTO.setToAll(false);
            messageDTO.setTitle("有流程传送给您");
            messageDTO.setContent("");
            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_2);
            messageDTO.setStartTime(new Date());
            messageDTO.setMsgAbstract("有新的流程抄送消息");
            messageDTO.setPublishingContent("有新的流程抄送消息，请查看");
            messageDTO.setBusKey(processInstance.getBusinessKey());
            messageDTO.setBusType(SysAnnmentTypeEnum.BPM.getType());
            messageDTO.setTemplateCode(CommonConstant.BPM_SERVICE_NOTICE_PROCESS);
            messageDTO.setIsRingBell(false);
            messageDTO.setRingDuration(0);
            messageDTO.setRingType(0);
            messageDTO.setType("");
            messageDTO.setData(map);
            messageDTO.setTaskId(currentTask.getId());

            messageDTO.setProcessInstanceId(currentTask.getProcessInstanceId());

            List<String> processDefinitionIdList = StrUtil.split(processInstance.getProcessDefinitionId(), ':');
            if (CollectionUtil.isNotEmpty(processDefinitionIdList) && processDefinitionIdList.size()>0) {
                // 流程标识
                String modkelKey = processDefinitionIdList.get(0);
                LambdaQueryWrapper<ActCustomModelInfo> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ActCustomModelInfo::getModelKey, modkelKey).last("limit 1");
                IActCustomModelInfoService bean = SpringContextUtils.getBean(IActCustomModelInfoService.class);
                ActCustomModelInfo one = bean.getOne(wrapper);
                if (Objects.nonNull(one)) {
                    String name = StrUtil.contains(one.getName(), "流程") ? one.getName() : one.getName()+"流程";
                    messageDTO.setProcessCode(one.getModelKey());
                    messageDTO.setProcessDefinitionKey(one.getModelKey());
                    messageDTO.setProcessName(name);
                }
            }
            messageDTO.setType("XT");
            messageDTO.setData(map);
            sysBaseAPI.sendTemplateMessage(messageDTO);
        }


    }
}
