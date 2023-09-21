package com.aiurt.modules.remind.job;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.job.service.JobHandler;
import org.flowable.job.service.impl.persistence.entity.JobEntity;
import org.flowable.variable.api.delegate.VariableScope;

/**
 * @author fgw 超时提醒处理器
 */
@Slf4j
public class TimeOutRemindJobHandler implements JobHandler {

    public static final String TYPE = "timeout-remind-handler";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void execute(JobEntity jobEntity, String s, VariableScope variableScope, CommandContext commandContext) {
        // 执行id
        String executionId = jobEntity.getExecutionId();
        //todo 发送消息
    }
}
