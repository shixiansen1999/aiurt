package com.aiurt.modules.user.pipeline;

import com.aiurt.modules.user.pipeline.context.UserContext;

/**
 * @author fgw
 */
public abstract class AbstractUserFilter<T extends UserContext> implements UserFilter<T> {

    /**
     * 实际任务处理
     * @param context
     * @param chain
     */
    @Override
    public void doFilter(T context, UserFilterChain chain) {
        if (context.getFilterSelector().matchFilter(this.getClass().getSimpleName())) {
            // 业务处理
            handle(context);
        }

        if (context.continueChain()) {
            // 执行下一个过滤器
            chain.fireNext(context);
        }
    }

    /**
     * 具体执行
     * @param context
     */
    protected abstract void handle(T context);
}
