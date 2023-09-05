package com.aiurt.modules.remind.handlers;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.remind.context.FlowRemindContext;

/**
 * <p>提醒规则校验</p>
 * @author fgw
 */
public class RemindRuleVerifyHandler extends AbstractFlowHandler<FlowRemindContext> {


    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(FlowRemindContext context) {
        // 仅发起人可催办流程，且可以对每个流程节点每 5 分钟催办一次；
    }
}
