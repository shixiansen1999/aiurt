package com.aiurt.modules.remind.pipeline;

import com.aiurt.modules.remind.pipeline.context.RemindContext;

import java.util.Objects;

/**
 * @author fgw
 */
public class DefaultRemindFilterChain<T extends RemindContext> implements RemindFilterChain<T> {


    /**
     * 下一个联调
     */
    private RemindFilterChain<T> next;

    /**
     *
     */
    private RemindFilter<T> filter;


    public DefaultRemindFilterChain(RemindFilterChain chain, RemindFilter filter) {
        this.next = chain;
        this.filter = filter;
    }

    /**
     * 事件处理流程
     *
     * @param context
     */
    @Override
    public void handle(T context) {
        this.filter.doFilter(context, this);
    }

    /**
     * 开启下一个鉴权
     *
     * @param ctx
     */
    @Override
    public void fireNext(T ctx) {
        RemindFilterChain<T> nextChain = this.next;
        if (Objects.nonNull(nextChain)) {
            nextChain.handle(ctx);
        }
    }
}
