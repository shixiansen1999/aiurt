package com.aiurt.modules.remind.pipeline;

import com.aiurt.modules.remind.pipeline.context.RemindContext;
import com.aiurt.modules.user.pipeline.UserFilterChain;
import com.aiurt.modules.user.pipeline.context.UserContext;

/**
 * @author fgw
 */
public interface RemindFilter<T extends RemindContext> {

    /**
     * 过滤逻辑封装点
     *
     * @param context
     * @param chain
     */
    void doFilter(T context, RemindFilterChain chain);
}
