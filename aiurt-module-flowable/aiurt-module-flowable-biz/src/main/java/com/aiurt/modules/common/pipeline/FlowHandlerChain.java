package com.aiurt.modules.common.pipeline;

import com.aiurt.modules.common.pipeline.context.FlowContext;

/**
 * @author fgw
 */
public interface FlowHandlerChain<T extends FlowContext> {

    /**
     * 事件处理流程
     * @param context
     */
    void handle(T context);

    /**
     * 开启下一个鉴权
     * @param ctx
     */
    void fireNext(T ctx);
}
