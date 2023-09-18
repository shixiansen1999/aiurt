package com.aiurt.modules.deduplicate.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import org.springframework.stereotype.Component;

/**
 * 加签
 * @author fgw
 */
@Component
public class MultiInstanceRuleVerifyHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {


    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {

    }
}
