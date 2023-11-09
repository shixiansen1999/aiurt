package com.aiurt.modules.recall.handler;

import cn.hutool.core.date.DateUtil;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.message.dto.HistoricProcessInstanceMessage;
import com.aiurt.modules.message.service.ISysFlowMessageService;
import com.aiurt.modules.recall.context.FlowRecallContext;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
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
public class RecallSendMessageHandler extends AbstractFlowHandler<FlowRecallContext> {

    @Autowired
    private ISysFlowMessageService sysMessageService;

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(FlowRecallContext context) {
        // 流程实例
        HistoricProcessInstance processInstance = context.getProcessInstance();
        // 代办任务
        List<Task> taskList = context.getTaskList();

        Date startTime = processInstance.getStartTime();
        String createTime = DateUtil.format(startTime, "yyyy-MM-dd HH:mm");

        // 模板中的消息数据
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("creatBy", context.getRealName());
        map.put("creatTime", createTime);
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, processInstance.getBusinessKey());
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.BPM.getType());

        HistoricProcessInstanceMessage message = new HistoricProcessInstanceMessage();
        message.setHistoricProcessInstance(processInstance);
        message.setTaskList(taskList);
        message.setTemplateCode("bpm_service_recall_process");
        message.setMsgAbstract("有流程【撤回】提醒");
        message.setType("XT");
        message.setMap(map);
        message.setUserName(processInstance.getStartUserId());


        List<String> list = taskList.stream().map(Task::getAssignee).collect(Collectors.toList());
        context.setUserNameList(list);

        sysMessageService.sendMessage(message);
    }
}
