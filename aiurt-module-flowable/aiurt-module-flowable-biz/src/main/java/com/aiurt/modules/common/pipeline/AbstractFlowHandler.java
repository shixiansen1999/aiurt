package com.aiurt.modules.common.pipeline;

import com.aiurt.modules.common.pipeline.context.FlowContext;

/**
 * @author fgw
 */
public abstract class AbstractFlowHandler<T extends FlowContext> implements FlowHandler<T> {


    /**
     * 过滤逻辑封装点
     *
     * @param context
     * @param chain
     */
    @Override
    public void doHandle(T context, FlowHandlerChain chain) {
        if (context.getHandlerSelector().matchHandler(this.getClass().getSimpleName())) {
            // 业务处理
            handle(context);
        }

        if (context.continueChain()) {
            // 执行下一个过滤器
            chain.fireNext(context);
        }
    }

    /**
     * 执行任务
     * @param context
     */
    public abstract void handle(T context);
}
