package com.aiurt.modules.user.pipeline;

import com.aiurt.modules.user.pipeline.context.UserContext;


/**
 * 过滤器链
 * @author fgw
 */
public interface UserFilterChain<T extends UserContext>{

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
