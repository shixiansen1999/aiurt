package com.aiurt.modules.deduplicate.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;

/**
 * <p>构造上下文</p>
 * @author fgw
 */
public class BuildDeduplicateContextHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {
    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {

    }
}
