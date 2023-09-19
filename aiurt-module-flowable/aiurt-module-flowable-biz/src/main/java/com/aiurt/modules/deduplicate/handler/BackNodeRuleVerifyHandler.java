package com.aiurt.modules.deduplicate.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author fgw
 */
@Slf4j
@Component
public class BackNodeRuleVerifyHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {


    private static final String REJECT = "reject" ;

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {

    }
}
