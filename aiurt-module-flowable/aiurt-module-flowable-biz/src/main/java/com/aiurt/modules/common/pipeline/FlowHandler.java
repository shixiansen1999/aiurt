package com.aiurt.modules.common.pipeline;

import com.aiurt.modules.common.pipeline.context.FlowContext;

/**
 * @author fgw
 */
public interface FlowHandler<T extends FlowContext> {

    /**
     * 过滤逻辑封装点
     *
     * @param context
     * @param chain
     */
    void doHandle(T context, FlowHandlerChain chain);

}
