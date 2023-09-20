package com.aiurt.modules.common.pipeline;

import com.aiurt.modules.common.pipeline.context.FlowContext;

import java.util.Objects;

/**
 * @author fgw
 */
public class DefaultFlowHandlerChain<T extends FlowContext> implements FlowHandlerChain<T> {

    /**
     * 下一个办理
     */
    private FlowHandlerChain<T> next;

    /**
     *
     */
    private FlowHandler<T> handler;


    public DefaultFlowHandlerChain(FlowHandlerChain chain, FlowHandler handler) {
        this.next = chain;
        this.handler = handler;
    }

    /**
     * 事件处理流程
     *
     * @param context
     */
    @Override
    public void handle(T context) {
        handler.doHandle(context, this);
    }

    /**
     * 开启下一个鉴权
     *
     * @param ctx
     */
    @Override
    public void fireNext(T ctx) {
        FlowHandlerChain<T> next = this.next;
        if (Objects.nonNull(next)) {
            next.handle(ctx);
        }
    }
}
