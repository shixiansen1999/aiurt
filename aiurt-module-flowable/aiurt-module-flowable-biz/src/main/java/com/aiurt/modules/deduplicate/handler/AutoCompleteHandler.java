package com.aiurt.modules.deduplicate.handler;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.flow.dto.FlowTaskCompleteCommentDTO;
import com.aiurt.modules.flow.dto.NextNodeUserDTO;
import com.aiurt.modules.flow.dto.TaskCompleteDTO;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.multideal.service.IMultiInTaskService;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.service.DefaultSelectUserService;
import com.aiurt.modules.user.service.IActCustomUserService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Slf4j
@Component
public class AutoCompleteHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {

    @Autowired
    private FlowApiService flowApiService;

    @Autowired
    private IMultiInTaskService multiInTaskService;

    @Autowired
    private FlowElementUtil flowElementUtil;

    @Autowired
    private IActCustomUserService customUserService;

    @Autowired
    private DefaultSelectUserService defaultSelectUser;

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {
        // 自动提交，判断是否正常逻辑，不管是否，都是满配选人
        // 判断是否

        Task task = context.getTask();
        Map<String, Object> caseVariables = task.getCaseVariables();
        //{
        //  "busData": {},
        //  "flowTaskCompleteDTO": {
        //    "approvalType": "agree",
        //    "comment": "统一",
        //    "delegateAssignee": ""
        //  },
        //  "taskId": "174878f1464411ee8b180242ac110005",
        //  "processInstanceId": "16dd5af0464411ee8b180242ac110005"
        //}

        caseVariables.put("approvalType","agree");
        Execution execution = context.getExecution();
        ProcessInstance processInstance = context.getProcessInstance();
        // 是否自动选人
        ActCustomTaskExt actCustomTaskExt = context.getActCustomTaskExt();
        FlowElement flowElement = flowElementUtil.getFlowElement(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        List<FlowElement> targetFlowElement = flowElementUtil.getTargetFlowElement(execution, flowElement, caseVariables);
        if (CollUtil.isEmpty(targetFlowElement)) {
            return;
        }

        TaskCompleteDTO taskCompleteDTO = new TaskCompleteDTO();
        FlowTaskCompleteCommentDTO completeCommentDTO = new FlowTaskCompleteCommentDTO();
        if (Objects.nonNull(actCustomTaskExt)) {
            Integer isAutoSelect = Optional.ofNullable(actCustomTaskExt.getIsAutoSelect()).orElse(1);
            if (isAutoSelect == 0) {
                Boolean completeTask = multiInTaskService.isCompleteTask(task);
                if (completeTask) {
                    List<NextNodeUserDTO> nodeUserDTOList = targetFlowElement.stream().filter(element-> element instanceof UserTask).map(element -> {
                        NextNodeUserDTO nextNodeUserDTO = new NextNodeUserDTO();
                        nextNodeUserDTO.setNodeId(element.getId());
                        ActCustomUser actCustomUser = customUserService.getActCustomUserByTaskInfo(task.getProcessDefinitionId(), element.getId(), "0");
                        List<String> userList = defaultSelectUser.getEmptyUserList(actCustomUser, caseVariables, processInstance);
                        nextNodeUserDTO.setApprover(userList);
                        return nextNodeUserDTO;
                    }).collect(Collectors.toList());
                    completeCommentDTO.setNextNodeUserParam(nodeUserDTOList);
                }
            }
        }

        completeCommentDTO.setApprovalType("agree");
        completeCommentDTO.setComment("审批去重");
        taskCompleteDTO.setTaskId(task.getId());
        taskCompleteDTO.setProcessInstanceId(task.getProcessInstanceId());

        taskCompleteDTO.setFlowTaskCompleteDTO(completeCommentDTO);
        try {
            taskCompleteDTO.setIsCheckAssign(false);
            flowApiService.completeTask(taskCompleteDTO);
            log.info("审批去重成功，taskId:{}, 用户:{}", task.getId(), task.getAssignee());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
