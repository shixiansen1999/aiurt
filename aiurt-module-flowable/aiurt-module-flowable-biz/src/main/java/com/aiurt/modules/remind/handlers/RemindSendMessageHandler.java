package com.aiurt.modules.remind.handlers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.remind.context.FlowRemindContext;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>发送消息</p>
 * @author fgw
 */
@Component
public class RemindSendMessageHandler extends AbstractFlowHandler<FlowRemindContext> {

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(FlowRemindContext context) {

        String loginName = context.getLoginName();

        String realName = context.getRealName();

        HistoricProcessInstance processInstance = context.getProcessInstance();

        // 代办任务
        List<Task> taskList = context.getTaskList();
        // 过滤发起人的任务
        List<Task> remindList = taskList.stream().filter(task -> !StrUtil.equalsIgnoreCase(task.getAssignee(), loginName))
                .collect(Collectors.toList());

        List<String> list = remindList.stream().map(Task::getAssignee).collect(Collectors.toList());
        context.setUserNameList(list);
        Date startTime = processInstance.getStartTime();
        String createTime = DateUtil.format(startTime, "yyyy-MM-dd");

        // 发送消息
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("creatBy", realName);
        map.put("creatTime", createTime);
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, processInstance.getBusinessKey());
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.BPM.getType());


        List<MessageDTO> messageDTOList = remindList.stream().map(task -> {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setIsMarkdown(true);
            messageDTO.setFromUser(loginName);
            messageDTO.setToUser(task.getAssignee());
            messageDTO.setToAll(false);
            messageDTO.setTitle(processInstance.getProcessDefinitionName());
            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_2);
            messageDTO.setStartTime(new Date());
            messageDTO.setMsgAbstract("有流程【催办】提醒");
            messageDTO.setPublishingContent("你有一条新的流程催办，请尽快处理");
            messageDTO.setBusKey(processInstance.getBusinessKey());
            messageDTO.setBusType(SysAnnmentTypeEnum.BPM.getType());
            messageDTO.setTemplateCode(CommonConstant.BPM_SERVICE_NOTICE_PROCESS);
            messageDTO.setIsRingBell(false);
            messageDTO.setRingDuration(0);
            messageDTO.setRingType(0);
            messageDTO.setType("");
            messageDTO.setData(map);
            messageDTO.setTaskId(task.getId());

            messageDTO.setProcessInstanceId(task.getProcessInstanceId());
            String name = StrUtil.contains(processInstance.getProcessDefinitionName(), "流程") ? processInstance.getProcessDefinitionName() : processInstance.getProcessDefinitionName() + "流程";
            messageDTO.setProcessCode(processInstance.getProcessDefinitionKey());
            messageDTO.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
            messageDTO.setProcessName(name);
            messageDTO.setType("XT");
            messageDTO.setData(map);
            return messageDTO;
        }).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(messageDTOList)) {
            messageDTOList.stream().forEach(messageDTO -> {
                sysBaseAPI.sendTemplateMessage(messageDTO);
            });
        }
    }
}
