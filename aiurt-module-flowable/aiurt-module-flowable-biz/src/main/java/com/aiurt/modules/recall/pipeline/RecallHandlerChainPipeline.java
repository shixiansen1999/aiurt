package com.aiurt.modules.recall.pipeline;

import com.aiurt.modules.common.pipeline.DefaultFlowHandlerChain;
import com.aiurt.modules.common.pipeline.FlowHandler;

/**
 * @author fgw
 */
public class RecallHandlerChainPipeline <T extends FlowHandler> {

    private DefaultFlowHandlerChain last;

    public RecallHandlerChainPipeline() {
    }

    public DefaultFlowHandlerChain getFilterChain() {
        return this.last;
    }

    public RecallHandlerChainPipeline addFirst(T filter) {
        DefaultFlowHandlerChain newChain = new DefaultFlowHandlerChain(this.last, filter);
        this.last = newChain;
        return this;
    }

    public RecallHandlerChainPipeline addFirst(String desc, T filter) {
        DefaultFlowHandlerChain newChain = new DefaultFlowHandlerChain(this.last, filter);
        this.last = newChain;
        return this;
    }
}
