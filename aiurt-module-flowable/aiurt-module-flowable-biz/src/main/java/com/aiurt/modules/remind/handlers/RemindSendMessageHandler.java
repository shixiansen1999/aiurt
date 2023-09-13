package com.aiurt.modules.remind.handlers;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.remind.context.FlowRemindContext;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>发送消息</p>
 * @author fgw
 */
public class RemindSendMessageHandler extends AbstractFlowHandler<FlowRemindContext> {

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(FlowRemindContext context) {

        String loginName = context.getLoginName();

        //
        List<Task> taskList = context.getTaskList();

        List<Task> remindList = taskList.stream().filter(task -> !StrUtil.equalsIgnoreCase(task.getAssignee(), loginName))
                .collect(Collectors.toList());
        //todo  发送消息
        // 使用线程池发送消息
    }
}
