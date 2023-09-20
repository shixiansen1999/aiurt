package com.aiurt.modules.recall.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.recall.context.FlowRecallContext;
import org.springframework.stereotype.Service;

/**
 * @author fgw
 */
@Service
public class BuildRecallContextHandler<T extends FlowRecallContext> extends AbstractFlowHandler<T> {

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {

    }
}
