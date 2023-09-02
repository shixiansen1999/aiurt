package com.aiurt.modules.remind.pipeline;

import com.aiurt.modules.remind.pipeline.context.RemindContext;

/**
 * @author fgw
 */
public abstract class AbstractRemindFilter <T extends RemindContext> implements RemindFilter<T> {


    /**
     * 过滤逻辑封装点
     *
     * @param context
     * @param chain
     */
    @Override
    public void doFilter(T context, RemindFilterChain chain) {
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
