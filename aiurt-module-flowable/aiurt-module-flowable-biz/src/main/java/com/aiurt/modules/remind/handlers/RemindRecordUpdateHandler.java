package com.aiurt.modules.remind.handlers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.entity.ActCustomRemindRecord;
import com.aiurt.modules.remind.service.IActCustomRemindRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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
        ActCustomRemindRecord actCustomRemindRecord = BeanUtil.copyProperties(lastRemindRecord, ActCustomRemindRecord.class, "id", "lastRemindTime", "receiveUserName");
        actCustomRemindRecord.setLastRemindTime(new Date());
        actCustomRemindRecord.setProcessInstanceId(context.getProcessInstanceId());
        actCustomRemindRecord.setRemindUserName(context.getLoginName());
        actCustomRemindRecord.setReceiveUserName(StrUtil.join(",", context.getUserNameList()));
        remindRecordService.save(actCustomRemindRecord);
    }
}
