package com.aiurt.modules.remind.handlers;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.common.pipeline.FlowHandlerChain;
import com.aiurt.modules.remind.context.FlowRemindContext;
import org.springframework.stereotype.Component;

/**
 * @author fgw
 */
@Component
public class BuildContextHandler extends AbstractFlowHandler<FlowRemindContext> {


    /**
     * 过滤逻辑封装点
     *
     * @param context
     * @param chain
     */
    @Override
    public void doHandle(FlowRemindContext context, FlowHandlerChain chain) {
        super.doHandle(context, chain);
    }

    /**
     * 具体执行
     *
     * @param context
     */
    @Override
    public void handle(FlowRemindContext context) {
        // 具体逻辑实现
    }
}
