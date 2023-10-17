package com.aiurt.modules.remind.handlers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.entity.ActCustomRemindRecord;
import com.aiurt.modules.remind.service.IActCustomRemindRecordService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>更新流程催办记录</p>
 * @author fgw
 */
@Component
public class RemindRecordUpdateHandler extends AbstractFlowHandler<FlowRemindContext> {

    @Autowired
    private IActCustomRemindRecordService remindRecordService;

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(FlowRemindContext context) {
        ActCustomRemindRecord lastRemindRecord = context.getLastRemindRecord();
        List<Task> taskList = context.getTaskList();
        List<String> list = taskList.stream().map(Task::getId).collect(Collectors.toList());
        ActCustomRemindRecord actCustomRemindRecord = BeanUtil.copyProperties(lastRemindRecord, ActCustomRemindRecord.class, "id", "lastRemindTime", "receiveUserName");
        actCustomRemindRecord.setLastRemindTime(new Date());
        actCustomRemindRecord.setProcessInstanceId(context.getProcessInstanceId());
        actCustomRemindRecord.setRemindUserName(context.getLoginName());
        actCustomRemindRecord.setTaskId(Objects.isNull(lastRemindRecord) ? StrUtil.join(",", list) : lastRemindRecord.getTaskId());
        actCustomRemindRecord.setReceiveUserName(StrUtil.join(",", context.getUserNameList()));
        remindRecordService.save(actCustomRemindRecord);
    }
}
