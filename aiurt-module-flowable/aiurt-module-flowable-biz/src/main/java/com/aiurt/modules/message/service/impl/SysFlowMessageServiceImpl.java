package com.aiurt.modules.message.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.message.dto.AbstractMessage;
import com.aiurt.modules.message.service.ISysFlowMessageService;
import org.flowable.task.api.Task;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaowe
 */
@Service
public class SysFlowMessageServiceImpl implements ISysFlowMessageService {

    @Autowired
    private ISysBaseAPI sysBaseApi;


    @Override
    public void sendMessage(AbstractMessage message) {

        // 消息模板的数据
        List<Task> taskList = message.getTaskList();

        List<MessageDTO> messageDTOList = taskList.stream().map(task -> {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setIsMarkdown(true);
            messageDTO.setFromUser(message.getUserName());
            messageDTO.setToUser(task.getAssignee());
            messageDTO.setToAll(false);
            String processDefinitionName = message.getProcessDefinitionName();
            String name = StrUtil.contains(processDefinitionName, "流程") ? processDefinitionName : processDefinitionName + "流程";
            messageDTO.setTitle(name);
            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_2);
            messageDTO.setStartTime(new Date());
            messageDTO.setMsgAbstract(message.getMsgAbstract());
            messageDTO.setPublishingContent(message.getPublishingContent());
            messageDTO.setBusKey(message.getBusKey());
            messageDTO.setBusType(SysAnnmentTypeEnum.BPM.getType());
            messageDTO.setTemplateCode(message.getTemplateCode());
            messageDTO.setIsRingBell(false);
            messageDTO.setRingDuration(0);
            messageDTO.setRingType(0);
            messageDTO.setData(message.getMap());
            messageDTO.setTaskId(task.getId());
            messageDTO.setProcessInstanceId(message.getProcessInstanceId());
            messageDTO.setProcessCode(message.getProcessDefinitionKey());
            messageDTO.setProcessDefinitionKey(message.getProcessDefinitionKey());
            messageDTO.setProcessName(name);
            messageDTO.setType("XT");
            return messageDTO;
        }).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(messageDTOList)) {
            messageDTOList.forEach(messageDTO -> sysBaseApi.sendTemplateMessage(messageDTO));
        }



    }
}
