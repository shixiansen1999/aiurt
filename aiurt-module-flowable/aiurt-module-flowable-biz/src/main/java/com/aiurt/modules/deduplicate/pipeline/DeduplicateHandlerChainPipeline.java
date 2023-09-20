package com.aiurt.modules.deduplicate.pipeline;

import com.aiurt.modules.common.pipeline.DefaultFlowHandlerChain;
import com.aiurt.modules.common.pipeline.FlowHandler;
import com.aiurt.modules.remind.pipeline.RemindHandlerChainPipeline;

/**
 * @author fgw
 */
public class DeduplicateHandlerChainPipeline<T extends FlowHandler> {


    private DefaultFlowHandlerChain last;

    public DeduplicateHandlerChainPipeline() {
    }

    public DefaultFlowHandlerChain getFilterChain() {
        return this.last;
    }

    public DeduplicateHandlerChainPipeline addFirst(T filter) {
        DefaultFlowHandlerChain newChain = new DefaultFlowHandlerChain(this.last, filter);
        this.last = newChain;
        return this;
    }

    public DeduplicateHandlerChainPipeline addFirst(String desc, T filter) {
        DefaultFlowHandlerChain newChain = new DefaultFlowHandlerChain(this.last, filter);
        this.last = newChain;
        return this;
    }
}
