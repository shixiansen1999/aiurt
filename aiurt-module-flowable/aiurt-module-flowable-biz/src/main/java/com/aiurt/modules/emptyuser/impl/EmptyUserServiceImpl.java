package com.aiurt.modules.emptyuser.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.common.pipeline.selector.LocalListBasedHandlerSelector;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.deduplicate.handler.AutoCompleteHandler;
import com.aiurt.modules.deduplicate.handler.BuildDeduplicateContextHandler;
import com.aiurt.modules.deduplicate.pipeline.DeduplicateHandlerChainPipeline;
import com.aiurt.modules.emptyuser.IEmptyUserService;
import com.aiurt.modules.message.dto.ProcessInstanceMessage;
import com.aiurt.modules.message.service.ISysFlowMessageService;
import com.aiurt.modules.user.enums.EmptyRuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
public class EmptyUserServiceImpl implements IEmptyUserService {

    @Autowired
    private TaskService taskService;


    @Autowired
    private DeduplicateHandlerChainPipeline deduplicateHandlerChainPipeline;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Autowired
    private ISysFlowMessageService messageService;

    /**
     * 审批人为空处理
     *
     * @param taskEntity
     */
    @Override
    public void handEmptyUserName(TaskEntity taskEntity) {
        String assignee = taskEntity.getAssignee();

        if (StrUtil.equalsIgnoreCase(EmptyRuleEnum.AUTO_ADMIN.getMessage(), assignee)) {
            taskService.setAssignee(taskEntity.getId(), "admin");

            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(taskEntity.getProcessInstanceId()).singleResult();
            HashMap<String, Object> map = new HashMap<>(16);

            String startUserId = processInstance.getStartUserId();
            LoginUser loginUser = sysBaseApi.getUserByName(startUserId);
            Date startTime = processInstance.getStartTime();
            String createTime = DateUtil.format(startTime, "yyyy-MM-dd HH:mm");

            map.put("creatBy", loginUser.getRealname());
            map.put("creatTime", createTime);
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, processInstance.getBusinessKey());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.BPM.getType());

            String processDefinitionName = processInstance.getProcessDefinitionName();
            String name = StrUtil.contains(processDefinitionName, "流程") ? processDefinitionName : processDefinitionName + "流程";

            ProcessInstanceMessage message = new ProcessInstanceMessage();
            message.setProcessInstance(processInstance);
            taskEntity.setAssignee("admin");
            message.setTaskList(Collections.singletonList(taskEntity));
            message.setMap(map);
            message.setTemplateCode("bpm_service_recall_process");
            message.setMsgAbstract("["+name+"]异常消息");
            message.setPublishingContent("当前节点["+taskEntity.getName()+"]审批人为空，需要进行设置，以保证流程的正常进行，请尽快处理！");
            message.setType("XT");
            message.setUserName("admin");

            // 发送消息
            messageService.sendMessage(message);
        }else {
            // 自动提交
            List<String> filterNames = new ArrayList<>();
            // 构造selector
            filterNames.add(BuildDeduplicateContextHandler.class.getSimpleName());
            filterNames.add(AutoCompleteHandler.class.getSimpleName());
            LocalListBasedHandlerSelector filterSelector = new LocalListBasedHandlerSelector(filterNames);
            FlowDeduplicateContext context = new FlowDeduplicateContext(filterSelector);
            context.setTask(taskEntity);
            deduplicateHandlerChainPipeline.getFilterChain().handle(context);
        }

    }
}
