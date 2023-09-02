package com.aiurt.modules.remind.pipeline;

import com.aiurt.modules.remind.pipeline.context.RemindContext;

/**
 * @author fgw
 */
public interface RemindFilterChain<T extends RemindContext> {


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
