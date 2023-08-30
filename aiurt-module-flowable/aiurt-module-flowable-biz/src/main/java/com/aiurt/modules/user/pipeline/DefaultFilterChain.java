package com.aiurt.modules.user.pipeline;

import com.aiurt.modules.user.pipeline.context.UserContext;

import java.util.Objects;

/**
 * <p>责任链</p>
 * @author fgw
 */
public class DefaultFilterChain<T extends UserContext> implements UserFilterChain<T>{

    /**
     * 下一个联调
     */
    private UserFilterChain<T> next;

    /**
     *
     */
    private UserFilter<T> filter;

    /**
     * 构造方法
     * @param chain
     * @param filter
     */
    public DefaultFilterChain(UserFilterChain chain, UserFilter filter) {
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
        UserFilterChain nextChain = this.next;
        if (Objects.nonNull(nextChain)) {
            nextChain.handle(ctx);
        }
    }
}
