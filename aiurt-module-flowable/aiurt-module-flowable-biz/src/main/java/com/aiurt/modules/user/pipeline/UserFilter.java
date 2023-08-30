package com.aiurt.modules.user.pipeline;

import com.aiurt.modules.user.pipeline.context.UserContext;


/**
 * @author fgw
 */
public interface UserFilter<T extends UserContext> {

    /**
     * 过滤逻辑封装点
     *
     * @param context
     * @param chain
     */
    void doFilter(T context, UserFilterChain chain);
}
