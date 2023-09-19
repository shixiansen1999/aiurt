package com.aiurt.modules.recall.service.impl;

import com.aiurt.modules.common.pipeline.selector.LocalListBasedHandlerSelector;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.aiurt.modules.flow.service.IActCustomTaskCommentService;
import com.aiurt.modules.modeler.service.IActCustomModelExtService;
import com.aiurt.modules.recall.context.FlowRecallContext;
import com.aiurt.modules.recall.dto.RecallReqDTO;
import com.aiurt.modules.recall.handler.BuildRecallContextHandler;
import com.aiurt.modules.recall.handler.ChangeTaskStatusHandler;
import com.aiurt.modules.recall.handler.RecallRuleVerifyHandler;
import com.aiurt.modules.recall.pipeline.RecallHandlerChainPipeline;
import com.aiurt.modules.recall.service.IFlowRecallService;
import com.aiurt.modules.remind.handlers.BuildContextHandler;
import com.aiurt.modules.remind.handlers.RemindRecordUpdateHandler;
import com.aiurt.modules.remind.handlers.RemindRuleVerifyHandler;
import com.aiurt.modules.remind.handlers.RemindSendMessageHandler;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fgw
 */
@Service
public class FlowRecallServiceImpl implements IFlowRecallService {

    @Autowired
    private RecallHandlerChainPipeline recallHandlerChainPipeline;
    @Autowired
    private IActCustomTaskCommentService actCustomTaskCommentService;
    @Autowired
    private TaskService taskService;

    /**
     * 撤回
     *
     * @param recallReqDTO
     */
    @Override
    public void recall(RecallReqDTO recallReqDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> filterNames = new ArrayList<>();
        // 构造selector
        filterNames.add(FlowDeduplicateContext.class.getSimpleName());
        filterNames.add(BuildRecallContextHandler.class.getSimpleName());
        filterNames.add(ChangeTaskStatusHandler.class.getSimpleName());
        filterNames.add(RecallRuleVerifyHandler.class.getSimpleName());
        LocalListBasedHandlerSelector filterSelector = new LocalListBasedHandlerSelector(filterNames);
        FlowRecallContext context = new FlowRecallContext(filterSelector);
        context.setProcessInstanceId(recallReqDTO.getProcessInstanceId());
        context.setRecallReason(recallReqDTO.getReason());
        context.setLoginName(loginUser.getUsername());
        context.setRealName(loginUser.getRealname());
        recallHandlerChainPipeline.getFilterChain().handle(context);
        //保存撤回原因
        List<Task> list = taskService.createTaskQuery()
                .processInstanceId(recallReqDTO.getProcessInstanceId())
                .list();
        List<ActCustomTaskComment> actCustomTaskComments = new ArrayList<>();
        for (Task task : list) {
            ActCustomTaskComment actCustomTaskComment = new ActCustomTaskComment();
            String recallReason = context.getRecallReason();
            String processInstanceId = recallReqDTO.getProcessInstanceId();
            actCustomTaskComment.setTaskId(task.getId());
            actCustomTaskComment.setTaskKey(task.getTaskDefinitionKey());
            actCustomTaskComment.setTaskName(task.getName());
            actCustomTaskComment.setCreateRealname(loginUser.getUsername());
            actCustomTaskComment.setComment(recallReason);
            actCustomTaskComment.setApprovalType("recall");
            actCustomTaskComment.setProcessInstanceId(processInstanceId);
            actCustomTaskComments.add(actCustomTaskComment);
        }
        actCustomTaskCommentService.saveBatch(actCustomTaskComments);


    }
}
